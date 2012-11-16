package net.frontlinesms.camel.smslib;

import java.util.LinkedList;
import java.util.Map;
import java.util.Date;

import org.smslib.CIncomingMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.NotConnectedException;
import org.smslib.service.MessageClass;

public class SmslibService {
	static { System.out.println("SmsLibService class loaded."); }
	
	private CService cService;

	private SmslibProducer producer;
	private SmslibConsumer consumer;
	
	private boolean consumerRunning;
	private boolean producerRunning;
	
	private MessageClass receiveMessageClass = MessageClass.ALL;

	private final CServiceFactory cServiceFactory;
	private final String uri;
	private final String remaining;
	private final Map<String, Object> parameters;

	private ReceiveThread receiveThread;

//> CONSTRUCTORS
	public SmslibService(CServiceFactory cServiceFactory, String uri, String remaining, Map<String, Object> parameters) {
		this.cServiceFactory = cServiceFactory;
		this.uri = uri;
		this.remaining = remaining;
		this.parameters = parameters;
		
		Object receiveMessageClass = parameters.get("receiveMessageClass");
		if(receiveMessageClass != null) {
			if(receiveMessageClass instanceof MessageClass)
				this.receiveMessageClass = (MessageClass) receiveMessageClass;
			else warn("Could not set receiveMessageClass to value " + receiveMessageClass +
					" of class " + receiveMessageClass.getClass());
		}
	}
	
	private synchronized void initCService() {
		this.cService = cServiceFactory.create(uri, remaining, parameters);
	}
	
//> ACCESSORS
	public boolean isConsumerRunning() {
		return consumerRunning;
	}
	
	public boolean isProducerRunning() {
		return producerRunning;
	}
	
	public void setReceiveMessageClass(MessageClass receiveMessageClass) {
		this.receiveMessageClass = receiveMessageClass;
	}

	public synchronized SmslibProducer getProducer() {
		return producer;
	}
	public synchronized void setProducer(SmslibProducer producer) throws SmslibServiceException {
		if(this.producer != null) {
			throw new SmslibServiceException("Producer already set.");
		}
		this.producer = producer;
	}

	public synchronized SmslibConsumer getConsumer() {
		return consumer;
	}
	public synchronized void setConsumer(SmslibConsumer consumer) throws SmslibServiceException {
		if(this.consumer != null) {
			throw new SmslibServiceException("Consumer already set.");
		}
		this.consumer = consumer;
	}
	
	public CService getCService() {
		return cService;
	}
	
	public ReceiveThread getReceiveThread() {
		return receiveThread;
	}
	
//> SERVICE METHODS
	public synchronized void startForConsumer() throws Exception {
		assert(consumer != null);
		consumerRunning = true;
		receiveThread = new ReceiveThread();
		receiveThread.start();
		startService();
	}

	public synchronized void startForProducer() throws Exception {
		assert(producer != null);
		producerRunning = true;
		try {
			startService();
		} catch(Exception ex) {
			producerRunning = false;
			producer = null;
			throw ex;
		}
	}
	
	public synchronized void stopForProducer(SmslibProducer p) throws Exception {
		if(producer == p) {
			producerRunning = false;
			stopIfUnused();
			producer = null;
		}
	}
	
	public synchronized void stopForConsumer(SmslibConsumer c) throws Exception {
		if(consumer == c) {
			consumerRunning = false;
			stopIfUnused();
			consumer = null;
			receiveThread.join();
			receiveThread = null;
		}
	}

	private synchronized void stopIfUnused() throws Exception {
		if(!producerRunning && !consumerRunning) {
			if(cService!=null && cService.isConnected()) {
				cService.disconnect();
			}
			cService = null;
		}
	}

//> SEND/RECEIVE METHODS
	private synchronized void startService() throws Exception {
		if(cService == null) {
			initCService();
		}
		if(!cService.isConnected()) {
			cService.connect();
		}
	}

	public void send(OutgoingSmslibCamelMessage message) throws Exception {
		COutgoingMessage cMess = message.getCMessage();
		this.cService.sendMessage(cMess);
		if(cMess.getRefNo() < 0) {
			throw new MessageRejectedException(cMess);
		}
		System.out.println("#vaneyck# SmslibService.send() "+(new Date().toString()));
	}

	public void doReceive() throws Exception {
		try {
			LinkedList<CIncomingMessage> messageList = new LinkedList<CIncomingMessage>();
			this.cService.readMessages(messageList, receiveMessageClass);
			for(CIncomingMessage m : messageList) {
				this.consumer.accept(new IncomingSmslibCamelMessage(m));
				// TODO deletion should be done in markRead() method in suitable class - sometimes deletion is
				// not desired and this behaviour should not be forced
				System.out.println("Deleting message...");
				this.cService.deleteMessage(m);
				System.out.println("Message deleted.");
			}
		} catch(NotConnectedException e) {
			System.out.println("SmslibService.doReceive() : NotConnectedException caught");
			this.consumer.handleDisconnection(e);
		}
	}
	
	public class ReceiveThread extends Thread {
		private boolean finished;
		
		public void run() {
			while(consumerRunning) {
				try {
					doReceive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			finished = true;
		}

		public boolean isFinished() {
			return finished;
		}
	}
	
//> LOGGING METHODS
	private void warn(String message) {
		log("WARN", message);
	}
	
	private void log(String level, String message) {
		System.out.println(level + ": " + message);
	}
}
