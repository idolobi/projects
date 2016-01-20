package com.es.deployer.deploy.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.es.deployer.common.Const;
import com.es.deployer.deploy.dto.DeployLogDto;
import com.es.deployer.deploy.dto.SourceDto;

public class DeployLogDao {
	private static Logger logger = Logger.getLogger(DeployLogDao.class);
	
	private SqlMapClientTemplate sqlMapClientTemplate;
	private TransactionTemplate transactionTemplate;
	
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
	    this.transactionTemplate = transactionTemplate;
	}
	
	/**
	 * 총 배포횟수
	 * @param paramMap : projId, depId, phsCd
	 * @return
	 */
	public int getDeployTimes(HashMap<String, String> paramMap) {
		return (Integer)sqlMapClientTemplate.queryForObject("DeployLog.SelectDeployTimes", paramMap);
	}
	
	/**
	 * 다음 배포횟수
	 * dep_freq
	 * @param paramMap
	 * @return
	 */
	public int getNextDeployFrequency(HashMap<String, String> paramMap) {
		return (Integer)sqlMapClientTemplate.queryForObject("DeployLog.SelectDeployFrequency", paramMap);
	}
	

	/**
	 * 배포소스로그저장
	 * @param svnDeploySourceLogDto
	 */
	public void addDeploySourceLog(DeployLogDto svnDeploySourceLogDto) {
		sqlMapClientTemplate.insert("DeployLog.InsertSvnDeploySourceLog", svnDeploySourceLogDto);
	}

	/**
	 * 제외목록
	 * @param paramMap : projId, depId, depPhsDivCd, phsCd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DeployLogDto> getExceptedSource(HashMap<String, String> paramMap) {
		List<DeployLogDto> rtnList = new ArrayList<DeployLogDto>();
		if(Const.PHASE_TEST_CD.equals(paramMap.get("depPhsDivCd"))) {
			rtnList = (List<DeployLogDto>)sqlMapClientTemplate.queryForList("DeployLog.SelectExceptedSourceForTest", paramMap);
		}
		if(Const.PHASE_REAL_CD.equals(paramMap.get("depPhsDivCd"))) {
			rtnList = (List<DeployLogDto>)sqlMapClientTemplate.queryForList("DeployLog.SelectExceptedSourceForReal", paramMap);
		}
		return rtnList; 
	}
	
	/**
	 * 롤백용 로그 기록의 수를 반환한다.
	 * @param depLogDto
	 * @return
	 */
	public Integer getRollbackSourceSeq(DeployLogDto depLogDto) {
		return (Integer) sqlMapClientTemplate.queryForObject("DeployLog.SelectRollbackSourceSeq", depLogDto);
	}
	
	/**
	 * 롤백정보 저장
	 * @param depSrcList
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int addRollbackSourceLog(final List<SourceDto> depSrcList, final HashMap<String, String> paramMap) throws Exception {
		final int depFreq = 0;
		int rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				for(int i = 0; i < depSrcList.size(); i++) {
					DeployLogDto depLogDto = new DeployLogDto();
					depLogDto.setProjId(paramMap.get("projId"));
					depLogDto.setDepId(paramMap.get("depId"));
					depLogDto.setDepPhsDivCd(paramMap.get("depPhsDivCd"));
					depLogDto.setDepFreq(depFreq);
					depLogDto.setSvnSrcUrl(depSrcList.get(i).getSvnSrcUrl());
					depLogDto.setSvnSrcNm(depSrcList.get(i).getSvnSrcNm());
					depLogDto.setSvnSrcRevNum(depSrcList.get(i).getSvnSrcRevNum());
					if(getRollbackSourceSeq(depLogDto) == 0) {
						addDeploySourceLog(depLogDto);
						savedCnt++;
					} else {
						logger.debug("########## 이미 롤백용 리비전이 저장되어 있습니다. " + depLogDto.getSvnSrcUrl() + "/" + depLogDto.getSvnSrcNm());
					}
				}
				return savedCnt;
			}
	    });
	    return rtn;
	}
	
	/**
	 * 배포된 소스 이력 저장
	 * @param depSrcList
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int addDeployedSourceLog(final List<SourceDto> depSrcList, final HashMap<String, String> paramMap) throws Exception {
		final int depFreq = getNextDeployFrequency(paramMap);
		int rtn = transactionTemplate.execute(new TransactionCallback<Integer>() {
			public Integer doInTransaction(TransactionStatus status) {
				Integer savedCnt = 0;
				for(int i = 0; i < depSrcList.size(); i++) {
					DeployLogDto depLogDto = new DeployLogDto();
					depLogDto.setProjId(paramMap.get("projId"));
					depLogDto.setDepId(paramMap.get("depId"));
					depLogDto.setDepPhsDivCd(paramMap.get("depPhsDivCd"));
					depLogDto.setDepFreq(depFreq);
					depLogDto.setProjId(paramMap.get("projId"));
					depLogDto.setSvnSrcUrl(depSrcList.get(i).getSvnSrcUrl());
					depLogDto.setSvnSrcNm(depSrcList.get(i).getSvnSrcNm());
					depLogDto.setSvnSrcRevNum(depSrcList.get(i).getSvnSrcRevNum());
					addDeploySourceLog(depLogDto);
					//System.out.println(depLogDto.getSvnSrcUrl());
					savedCnt++;
				}
				return savedCnt;
			}
	    });
	    return rtn;
	}
}
