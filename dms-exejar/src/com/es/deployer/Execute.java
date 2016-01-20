package com.es.deployer;

import org.springframework.context.ApplicationContext;

import com.es.deployer.common.Const;
import com.es.deployer.common.Parameter;
import com.es.deployer.config.ConfigLoader;
import com.es.deployer.deploy.service.Accept;
import com.es.deployer.deploy.service.Build;
import com.es.deployer.deploy.service.Deploy;
import com.es.deployer.deploy.service.DeployID;
import com.es.deployer.deploy.service.Merge;
import com.es.deployer.deploy.service.Notice;
import com.es.deployer.deploy.service.Pack;
import com.es.deployer.deploy.service.Release;
import com.es.deployer.deploy.service.Update;
import com.es.deployer.ftp.ServerInfo;

public class Execute {
	
	public static void main(String[] args) {
		Parameter param = new Parameter();
		if(args.length < 4) {
			System.out.println("★ 필수 파라미터 : service name, deploy id, test or real, action");
			return;
		} else {
			param.setService(args[0]); // ex, lottesuper2011fo
			param.setDepId(args[1]); // ex, FO_20130319_1330
			param.setPhase(args[2]); // dvlp, test or real
			param.setTask(args[3]); // accept, reaccept, receipt, update, merge, build, pack, deploy, record, notice, release
			

			/*
			if(args.length == 5) {
				param.setRglrUrgnt(args[4]); // U or null
				msg = msg + ", " + param.getRglrUrgnt() + "]";
			} else {
				msg = msg + "]";
			}
			
			System.out.println(msg);
			*/
		}
		
		ApplicationContext ac = new ConfigLoader().getContext();
		
		if("GETID".equals(param.getTask().toUpperCase())) {
			printParams(param);
			DeployID deployId = (DeployID) ac.getBean("deployId");
			try {
				deployId.setDeployId(param);
			} catch (Exception e) {
				System.out.println("★아이디 획득 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("ACCEPT".equals(param.getTask().toUpperCase())) {
			if(args.length >= 5) {
				param.setRglrUrgnt(args[4]);
				param.setDepReqId(args[5]);
			}
			printParams(param);
			
			Accept acpt = (Accept) ac.getBean("accept");
			try {
				acpt.acceptRequest(param);
			} catch (Exception e) {
				System.out.println("★ 접수 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("REACCEPT".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Accept acpt = (Accept) ac.getBean("accept");
			try {
				acpt.acceptRegularReRequest(param);
			} catch (Exception e) {
				System.out.println("★ 재접수 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}		
		
		if("RECEIPT".equals(param.getTask().toUpperCase())) { // 특정 배포ID 접수
			printParams(param);
			Accept acpt = (Accept) ac.getBean("accept");
			try {
				acpt.acceptSpecialDeployRequest(param);
			} catch (Exception e) {
				System.out.println("★ 접수 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("RELEASE".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Release rls = (Release) ac.getBean("release");
			try {
				rls.releaseRegularRequest(param);
			} catch (Exception e) {
				System.out.println("★ 잠금해제 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("UPDATE".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Update updt = (Update) ac.getBean("update");
			try {
				updt.update(param);
			} catch (Exception e) {
				System.out.println("★ UPDATE 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("MERGE".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Merge mrg = (Merge) ac.getBean("merge");
			try {
				mrg.merge(param);
			} catch (Exception e) {
				System.out.println("★ MERGE 오류 : " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("BUILD".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Build bld = (Build) ac.getBean("build");
			try {
				bld.build(param);
			} catch (Exception e) {
				System.out.println("★ BUILD 오류");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("PACK".equals(param.getTask().toUpperCase())) {
			printParams(param);
			Pack pck = (Pack) ac.getBean("pack");
			try {
				pck.pack(param);
			} catch (Exception e) {
				System.out.println("★ PACK 오류");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("DEPLOY".equals(param.getTask().toUpperCase())) {
			ServerInfo svrInfo = new ServerInfo(
					args[4], // ip 
					Integer.parseInt(args[5]), // port 
					args[6], // id
					args[7] // password
				);
			param.setSvrInfo(svrInfo);
			printParams(param);
			Deploy dep = (Deploy) ac.getBean("deploy");
			try {
				dep.deploy(param);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("RECORD".equals(param.getTask().toUpperCase())) {
			if(args.length >= 5) {
				param.setRglrUrgnt(args[4]);
				param.setDepReqId(args[5]);
			}
			printParams(param);
			
			Deploy dep = (Deploy) ac.getBean("deploy");
			try {
				if("DVLP".equals(param.getPhase().toUpperCase())) {
					dep.recordTestDeploy(param);
				} else if("TEST".equals(param.getPhase().toUpperCase())) {
					dep.recordTestDeploy(param);
				} else if("REAL".equals(param.getPhase().toUpperCase())) {
					dep.recordRealDeploy(param);
				} else {
					System.err.println("parameter error.");
					System.exit(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if("NOTICE".equals(param.getTask().toUpperCase())) {
			param.setBuild(args[4].toUpperCase()); // message type SUCCESS, FAIL
			printParams(param);
			Notice noti = (Notice) ac.getBean("notice");
			try {
				if(Const.PHASE_DVLP.equals(param.getPhase()) || 
						Const.PHASE_TEST.equals(param.getPhase())) {
					noti.noticeMail(param);	
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}		
	}
	
	private static void printParams(Parameter param) {
		String msg = "★ Parameters [" + param.getService() + ", "
				+ param.getDepId() + ", "
				+ param.getPhase() + ", "
				+ param.getTask() + ", "
				+ (param.getRglrUrgnt() == null ? "null" : param.getRglrUrgnt()) + ", "
				+ (param.getBuild() == null ? "null" : param.getBuild()) + ", "
				+ (param.getSvrInfo() == null ? "null" : param.getSvrInfo().getId() + "@" + param.getSvrInfo().getIp() + ":" + param.getSvrInfo().getPort()) + "]";
		System.out.println(msg);
	}
}
