package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_AGT_Service;

/**
 * Agent정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("AGT_AGT_Service")
public class AGT_AGT_ServiceImpl extends ServiceIMPL implements AGT_AGT_Service {

	@Resource(name="AGT_AGT_Mapper")
	private AGT_AGT_Mapper dao;

	/**
	 * Agent정보 조회
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> AGT_AGT_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		//user id 배열 처리
		String userId	= "";
		if (param.get("userId") != null) {
			if(param.get("userId") instanceof String) {
				userId	= String.valueOf(param.get("userId"));
			} else if (param.get("userId") instanceof List) {
				List<Object> userList	=  (List<Object>) param.get("userId");
				for (int i = 0; i < userList.size(); i++) {
					if (!"".equals(userId))
						userId += ",";
					userId	+= "'" + userList.get(i) + "'";
				}
			}
			
			param.put("userId", userId);
		}
		
		return dao.AGT_AGT_S1000J(param);
	}
	
	/**
	 * 인증서 정보 조회 - Agent ID로 조회
	 */
	@Override
	public List<Map<String, Object>> AGT_AGT_S1001J(Map<String, Object> param) throws Exception {
		return dao.AGT_AGT_S1001J(param);
	}
	

	/**
	 * Agent정보 조회 - Agent ID
	 */
	@Override
	public Map<String, Object> AGT_AGT_V1000J(Map<String, Object> param) throws Exception {
		return dao.AGT_AGT_V1000J(param);
	}

	/**
	 * Agent정보 등록
	 */
	@Override
	public void AGT_AGT_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			//해당하는 Agent가 이미 존재하는지 확인
			Map<String,Object> agent	= dao.AGT_AGT_V1000J(param);
			if (agent != null)
				throw new DefinedException("이미 등록 된 Agent정보 입니다.");
			
			dao.AGT_AGT_I1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Agent정보 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._911);
			throw ex;
		}
	}

	/**
	 * Agent정보 수정
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_AGT_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> agent	= dao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			dao.AGT_AGT_U1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Agent정보 수정 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._912);
			throw ex;
		}
	}

	/**
	 * Agent정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_AGT_D1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> agent	= dao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			dao.AGT_AGT_D1000J(param);	//Agent정보 삭제
			dao.AGT_AGT_D1001J(param);	//인증서정보 삭제
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Agent정보 삭제 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._913);
			throw ex;
		}
	}

}
