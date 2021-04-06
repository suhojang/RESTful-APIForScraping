package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * Agent정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface AGT_AGT_Service {

	/**
	 * Agent정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_AGT_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 인증서 정보 조회 - Agent ID로 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_AGT_S1001J(Map<String,Object> param) throws Exception;
	
	/**
	 * Agent정보 조회 - Agent ID
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> AGT_AGT_V1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * Agent정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_AGT_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * Agent정보 수정
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_AGT_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * Agent정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_AGT_D1000J(Map<String, Object> param) throws DefinedException;
}
