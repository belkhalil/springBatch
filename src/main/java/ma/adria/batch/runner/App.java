package ma.adria.batch.runner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ma.adria.batch.tasklet.FluxTasklet;

public class App {
	private static final Logger LOGGER = LoggerFactory.getLogger(FluxTasklet.class);
	
	private static Long CODE_ETAT;
    
    
	
//	private static Long nombreOperations;

	public static void main(String[] args) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		final long startTime = System.currentTimeMillis();	
		try{

		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("configuration-context.xml");

		JobLauncher jobLauncher = (JobLauncher) applicationContext.getBean("jobLauncher");
		Job job = (Job) applicationContext.getBean("FluxTaskletJob");
		    CODE_ETAT=Long.parseLong(args[0]);
			Date today = new Date();
			JobParametersBuilder paramsBuilder = new JobParametersBuilder();
			paramsBuilder.addLong("CODE_ETAT", CODE_ETAT);
			paramsBuilder.addDate("distinctDate", today);

			JobExecution execution = jobLauncher.run(job, paramsBuilder.toJobParameters());
			
			//ExitStatus stat = execution.getExitStatus();
			
//			if(stat.getExitCode().equals(ExitStatus.COMPLETED.getExitCode()))
//			{
//				System.out.println("Finished--------------");
//				System.exit(0);
//			}
//			else
//			{
//				List<Throwable> exs = execution.getAllFailureExceptions();
//				for(Throwable c : exs)
//				{
//					if(c instanceof Exception)
//					{
//						LOGGER.error(c.getMessage(), c);
//						System.exit(10);
//					}
//					else
//					{
//						LOGGER.error("unknown Exception");
//						System.exit(99);
//					}
//				}
//			}
				
		} catch (JobExecutionAlreadyRunningException ex) {
			LOGGER.error(ex.getMessage(),ex);
            System.exit(2);
        } catch (JobRestartException ex) {
        	LOGGER.error(ex.getMessage(),ex);
            System.exit(3);
        } catch (JobInstanceAlreadyCompleteException ex) {
        	LOGGER.error(ex.getMessage(),ex);
            System.exit(4);
        } catch (JobParametersInvalidException ex) {
        	LOGGER.error(ex.getMessage(),ex);
            System.exit(5);
        } catch (Exception ex) {
        	LOGGER.error(ex.getMessage(),ex);
            System.exit(7);
        } finally {
			//final long endTime = System.currentTimeMillis();
//			System.out.println("Finished ---" + "FluxTaskletJob" + " in: "
//					+ calculateDuration(startTime, endTime) + "MILLISECONDS!");
                        
		}
		
	}
		
	
	private static String calculateDuration(long startTime, long endTime) {
		final long duration = endTime - startTime;
		StringBuilder sb = new StringBuilder();
		sb.append(TimeUnit.MILLISECONDS.toHours(duration));
		sb.append(" h ");
		sb.append(TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(duration)));
		sb.append(" min ");
		sb.append(TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(duration)));
		sb.append(" sec ");
		return sb.toString();
	}

}