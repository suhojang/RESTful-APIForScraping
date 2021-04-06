package project.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_SYN_Service;
import project.service.CMN_CMN_Service;

/**
 * Agent정보 동기화
 * @author 장수호
 *
 */
@Service("AGT_SYN_Service")
public class AGT_SYN_ServiceImpl extends ServiceIMPL implements AGT_SYN_Service {
	
	@Resource(name="AGT_AGT_Mapper")
	private AGT_AGT_Mapper agentDao;

	@Resource(name="AGT_SYN_Mapper")
	private AGT_SYN_Mapper dao;

	/**
	 * Agent정보  동기화 - 삭제목록 반환
	 * @param param
	 * @throws DefinedException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> AGT_SYN_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		
		//인증서 삭제 목록
		List<Map<String,Object>> list	= new ArrayList<Map<String,Object>>();
		Map<String,Object> deleteCert	= null;
		
		Map<String,Object> params	= new HashMap<String,Object>();
		try {
			Map<String,Object> info	= agentDao.AGT_AGT_V1000J(param);
			if (info == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			Map<String,Object> agent	= (Map<String,Object>)param.get("agent");
			params.put("version", 	agent.get("version"));
			params.put("os", 		agent.get("os"));
			params.put("privateIP", agent.get("privateIP"));
			params.put("publicIP", 	agent.get("publicIP"));
			params.put("agentId", 	param.get("agentId"));
			params.put("timestamp", param.get("timestamp"));
			
			//agent정보 동기화
			dao.AGT_SYN_U1000J(params);
			
			List<Map<String,Object>> certs	= (List<Map<String,Object>>)param.get("certs");
			if (certs != null && certs.size() > 0) {
				params	= new HashMap<String,Object>();
				params.put("agentId", param.get("agentId"));
				
				CMN_CMN_Service cmnService	= (CMN_CMN_Service) ContextLoader.getCurrentWebApplicationContext().getBean("CMN_CMN_Service");
				
				for (int i = 0; i < certs.size(); i++) {
					Map<String,Object> certParam	= new HashMap<String,Object>();
					certParam.put("certId", String.valueOf(certs.get(i).get("certId")));
					certParam.put("userId", String.valueOf(certs.get(i).get("userId")));
					
					List<Map<String,Object>> certList	= dao.AGT_SYN_V1000J(certParam);
					if (certList != null && certList.size() > 0) {
						for (int j = 0; j < certList.size(); j++) {
							Map<String, Object> cert	= certList.get(j);
							
							String yn	= String.valueOf(cert.get("CRTINF_YN"));
							
							if ("Y".equals(yn)) {
								if (!param.get("agentId").equals(String.valueOf(cert.get("AGTINF_ID")))) {
									//인증서 삭제목록 추가
									deleteCert	= new HashMap<String,Object>();
									
									deleteCert.put("userId", 	certs.get(i).get("userId"));
									deleteCert.put("certId", 	certs.get(i).get("certId"));
									deleteCert.put("certnm",	certs.get(i).get("certnm"));
									deleteCert.put("expire", 	certs.get(i).get("expire"));
									
									list.add(deleteCert);
									
									break;
								}
							} else {
								if (param.get("agentId").equals(String.valueOf(cert.get("AGTINF_ID")))) {
									//인증서 삭제목록 추가
									deleteCert	= new HashMap<String,Object>();
									
									deleteCert.put("userId", 	certs.get(i).get("userId"));
									deleteCert.put("certId", 	certs.get(i).get("certId"));
									deleteCert.put("certnm",	certs.get(i).get("certnm"));
									deleteCert.put("expire", 	certs.get(i).get("expire"));
									
									list.add(deleteCert);
									
									break;
								}
							}
							/*
							if (!"Y".equals(yn)) {
								if (param.get("agentId").equals(String.valueOf(cert.get("AGTINF_ID")))) {
									//인증서 삭제목록 추가
									deleteCert	= new HashMap<String,Object>();
									
									deleteCert.put("userId", 	certs.get(i).get("userId"));
									deleteCert.put("certId", 	certs.get(i).get("certId"));
									deleteCert.put("certnm",	certs.get(i).get("certnm"));
									deleteCert.put("expire", 	certs.get(i).get("expire"));
									
									list.add(deleteCert);
								}
							}*/
						}
					} else {
						params	= new HashMap<String,Object>();
						
						params.put("crtinf_seq", 	cmnService.nextval("CRTINF_SEQ"));
						params.put("userId", 		certs.get(i).get("userId"));
						params.put("agentId", 		param.get("agentId"));
						params.put("certId", 		certs.get(i).get("certId"));
						params.put("certnm", 		certs.get(i).get("certnm"));
						params.put("expire", 		certs.get(i).get("expire"));
						
						//인증서 정보 추가 - 사용여부 'N'
						dao.AGT_SYN_I1000J(params);
						
						//인증서 삭제목록 추가
						deleteCert	= new HashMap<String,Object>();
						deleteCert.put("userId", 	certs.get(i).get("userId"));
						deleteCert.put("certId", 	certs.get(i).get("certId"));
						deleteCert.put("certnm",	certs.get(i).get("certnm"));
						deleteCert.put("expire", 	certs.get(i).get("expire"));
						
						list.add(deleteCert);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Agent정보 동기화 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._932);
			throw ex;
		}
		
		return list;
	}
}
