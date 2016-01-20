package com.es.deployer.deploy.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;

import com.es.deployer.common.Const;
import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.DeployDao;
import com.es.deployer.deploy.dao.RequestDao;
import com.es.deployer.deploy.dto.BaseRequestDto;
import com.es.deployer.deploy.dto.MailMessageDto;
import com.es.deployer.doc.dao.StatusDao;
import com.es.deployer.mail.MailClient;
import com.es.deployer.mail.MailMessage;

public class Notice {
	private static Logger logger = Logger.getLogger(Notice.class);
	
	private RequestDao requestDao;
	private DeployDao deployDao;
	private StatusDao  statusDao;
	
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	
	public void setDeployDao(DeployDao deployDao) {
		this.deployDao = deployDao;
	}
	
	public void setStatusDao(StatusDao statusDao) {
		this.statusDao = statusDao;
	}

	public void noticeMail(Parameter param) throws AddressException, MessagingException {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		logger.debug("########## Property File Name : " + param.getService()+"."+param.getPhase() + ".properties");
		
		final String docId = Const.DOC_REQUEST;
		
		String projId = prpt.getProjId();
		String srcGrupCd = prpt.getSrcGrupCd();
		String depId = param.getDepId();
		String build = param.getBuild();
		
		String phsCd = statusDao.getPhsCdOnEvtNm(projId, docId, Const.EVT_NM_RECEIPT);
		
		// 받는 사람
		List<BaseRequestDto> recipList = requestDao.selectRecipientsForMailMessage(projId, depId, phsCd);
		String to = "";
		String reqLst = "";
		for(int i = 0; i < recipList.size(); i++) {
			if(i == 0) {
				to = "<" + recipList.get(i).getDevprId() + "@lotte.com>";
			} else {
				to = to + ",<" + recipList.get(i).getDevprId() + "@lotte.com>";
			}
			String no =  ((i+1) < 10) ? ("0" + (i+1)) : Integer.toString(i+1);
			reqLst = reqLst + no + " [" + recipList.get(i).getSrcGrupCd() + "] " + recipList.get(i).getSubject() + "\r\n";
		}
		
		reqLst = "※ 배포 목록\r\n" + reqLst;
		
		// 메세지
		MailMessageDto mailMsgDto;
		if(Const.BUILD_SUCCESS.equals(build)) {
			// 빌드성공용 메세지
			// 메세지코드 : NR, UR, TT, HD
			mailMsgDto = deployDao.selectTestDeployMessage(projId, srcGrupCd, depId);
		} else {
			// 빌드오류용 메세지
			// 메세지코드 : ER
			mailMsgDto = deployDao.selectTestDeployFailMessage(projId, srcGrupCd, "ER");
			
			String path = Const.DEPLOYER_HOME + "/logs/" + param.getService();
			String file = param.getDepId() + "_BUILD.log";
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(path + "/" + file));
				String line = "";
				String msgCnts = mailMsgDto.getMsgCnts();
				while((line = br.readLine()) != null) {
					msgCnts = msgCnts + line + "\r\n";
				}
				mailMsgDto.setMsgCnts(msgCnts);
			} catch (FileNotFoundException e1) {
				mailMsgDto.setMsgCnts(path + "/" + file + " 이(가) 존재하지 않습니다.");
				
			} catch (IOException e) {
				mailMsgDto.setMsgCnts(path + "/" + file + " 을(를) 읽는데 실패하였습니다.");
				
			}
		}
		
		// 보내는 사람
		String sender = mailMsgDto.getDepManId()+"@lotte.com";
		
		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom(sender);
		mailMessage.setTo(to);
		mailMessage.setSubject(mailMsgDto.getMsgSubj());
		mailMessage.setText(mailMsgDto.getMsgCnts() + "\r\n" + reqLst);
		if(to != null && to.length() > 10) {
			MailClient mailClient = new MailClient(mailMessage);
			mailClient.sendMail(mailMessage);	
		}		
	}	
}
