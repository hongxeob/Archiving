package practice.query_and_stream_test.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimerAop {
	@Pointcut("@annotation(practice.query_and_stream_test.aop.Timer)")
	private void enableTimer() {
	}

	@Around("enableTimer()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object result = joinPoint.proceed();

		stopWatch.stop();

		System.out.println("============ 총 걸린 시간 : " + stopWatch.getTotalTimeMillis() + " ============");

		return result;
	}
}
