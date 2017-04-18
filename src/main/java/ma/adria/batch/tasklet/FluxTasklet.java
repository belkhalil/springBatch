package ma.adria.batch.tasklet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ma.adria.batch.exception.FluxException;
import ma.adria.batch.mapper.FluxMapper;
import ma.adria.batch.model.Flux;

/**
 * @author bahadi
 *
 */
public class FluxTasklet implements Tasklet, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(FluxTasklet.class);

//	private statc final String DATE_FORMAT = "yyyyMMdd";
//	private static final String DATEFORMAT = "ddMMyy";
//	public static final String STATUT_SIGNE = "Signe";
//	private static final String REF_VIR_DEBUT = "8";
//	private static final String REF_PRE_DEBUT = "9";

	private DataSource dataSource;
	
	private String CODE_ETAT;
	private String CODE_LANGUE;
	private String filePath;               
	private String selectFlux;
	private String insertFlux;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.println("Début traitement");
		Connection conn = getDataSource().getConnection();
		/*
		 * retreive a fux  from db with a parameter CODETRA
		 */
		NamedParameterJdbcTemplate myJDBC = new NamedParameterJdbcTemplate(getDataSource());
		
		// Assume a valid connection object conn
		conn.setAutoCommit(false);
		final long startTime = System.currentTimeMillis();
		//System.out.println("start time"+startTime);
		try {
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("CODE_ETAT", CODE_ETAT);
			List<Flux> flux = myJDBC.query(selectFlux, parameters, new FluxMapper());
			if (flux != null) {
				
				System.out.println(flux.toString());
//				File fout = new File(filePath);
//				FileOutputStream fos;
//				fos = new FileOutputStream(fout);
//				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//		        StringBuilder text = new StringBuilder("");
//		        text.append("Code: "+"CODE_ETAT: "+"CODETRA: "+"LIB_COURT: "+"LIBELLE: \n");
				
				for(Flux f:flux){
				Flux fluxToInsert=new Flux();
				fluxToInsert.setCODE(f.getCODE());
				fluxToInsert.setCODE_ETAT(f.getCODE_ETAT());
				//fluxToInsert.setCODETRA(f.getCODETRA());
				fluxToInsert.setLIBELLE(f.getLIBELLE());
				fluxToInsert.setTYPE_CODE(f.getTYPE_CODE());
				fluxToInsert.setCODE_LANGUE(this.getCODE_LANGUE());
				/**Insertion of flux to db
				 * we can use MapSqlParameterSource parametersFlux = new MapSqlParameterSource() 
				 * we will work by BeanPropertySqlParameterSource 				 */
				
				BeanPropertySqlParameterSource beanParamSource = new BeanPropertySqlParameterSource(fluxToInsert);
			 int res=myJDBC.update(insertFlux, beanParamSource);
			 System.out.println("requete------------------"+insertFlux);
			 if (res == 1) {
					conn.commit();
					System.out.println("Commit Success");

				} else if (res != 1) {
					// conn.rollback(roExtWrSavepoint);
					conn.rollback();
					throw new FluxException("Erreur insertion");
				}
				}
			}
		       
//		        
//		        text.append(f.getCODE()+" ")
//		        .append(f.getCODE_ETAT()+" ")
//		        .append(f.getCODETRA()+" ")
//		        .append(f.getLIB_COURT()+" ")
//		        .append(f.getLIBELLE());
//		        if(f!=null)
//		        text.append("\n");
		       
				
//		        bw.write(text.toString());
//		        bw.newLine();
//		        bw.close();
				 
				
				
				
			
			else {
				throw new FluxException("No result found withe this code: " );
			}

			
			
//		} catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
			
		} catch (DataAccessException e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		} catch (IndexOutOfBoundsException e) {
			// conn.rollback(roExtWrSavepoint);
			conn.rollback();
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		} catch(FluxException e){
			conn.rollback();
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}catch (Exception e) {
			conn.rollback();
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}finally {
			final long endTime = System.currentTimeMillis();
			//System.out.println("endTime time"+endTime);
			//long diffInMillis =    newerDate.getTime() - endTime.getTime()
			System.out.println("Finished ---" + "FluxTaskletJob" + " in: "
					+calculateDuration(endTime,startTime) + "MILLISECONDS");
                        
		}
		
		
		return 
			RepeatStatus.FINISHED;
			
			
		
	}
	private static Long calculateDuration(long endTime, long starttime) {
//		final long duration = endTime - startTime;
//		StringBuilder sb = new StringBuilder();
//		sb.append(TimeUnit.MILLISECONDS.toHours(duration));
//		sb.append(" h ");
//		sb.append(TimeUnit.MILLISECONDS.toMinutes(duration)
//				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
//						.toHours(duration)));
//		sb.append(" min ");
//		sb.append(TimeUnit.MILLISECONDS.toSeconds(duration)
//				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//						.toMillis(duration)));
//		sb.append(" sec ");
		Long difference=endTime-starttime;
		return difference;
	}
	/*
	 * getters and setters
	 */
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSelectFlux() {
		return selectFlux;
	}

	public void setSelectFlux(String selectFlux) {
		this.selectFlux = selectFlux;
	}

	public String getInsertFlux() {
		return insertFlux;
	}

	public void setInsertFlux(String insertFlux) {
		this.insertFlux = insertFlux;
	}

	
	public String getCODE_ETAT() {
		return CODE_ETAT;
	}

	public void setCODE_ETAT(String cODE_ETAT) {
		CODE_ETAT = cODE_ETAT;
	}


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	

	public String getCODE_LANGUE() {
		return CODE_LANGUE;
	}

	public void setCODE_LANGUE(String cODE_LANGUE) {
		CODE_LANGUE = cODE_LANGUE;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}