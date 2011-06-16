Building
--------
When building this project, I irregularly but frequently get failures in the SmslibServiceTest.  Not sure what to do about them.  Below is the output detailing one such failure.

-------------------------------------------------------------------------------
Test set: net.frontlinesms.camel.smslib.SmslibServiceTest
-------------------------------------------------------------------------------
Tests run: 12, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.224 sec <<< FAILURE!
testCServiceStopIfNoMoreUsers(net.frontlinesms.camel.smslib.SmslibServiceTest)  Time elapsed: 0.003 sec  <<< ERROR!
org.mockito.exceptions.base.MockitoException: 
'readMessages' is a *void method* and it *cannot* be stubbed with a *return value*!
Voids are usually stubbed with Throwables:
    doThrow(exception).when(mock).someVoidMethod();
If the method you are trying to stub is *overloaded* then make sure you are calling the right overloaded version.
	at net.frontlinesms.camel.smslib.SmslibServiceTest.testCServiceStopIfNoMoreUsers(SmslibServiceTest.java:144)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:31)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:76)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.apache.maven.surefire.junit4.JUnit4TestSet.execute(JUnit4TestSet.java:62)
	at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.executeTestSet(AbstractDirectoryTestSuite.java:140)
	at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.execute(AbstractDirectoryTestSuite.java:165)
	at org.apache.maven.surefire.Surefire.run(Surefire.java:107)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at org.apache.maven.surefire.booter.SurefireBooter.runSuitesInProcess(SurefireBooter.java:289)
	at org.apache.maven.surefire.booter.SurefireBooter.main(SurefireBooter.java:1005)


