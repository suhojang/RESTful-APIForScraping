package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * 인증서정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface AGT_CRT_Service {

	/**
	 * 인증서정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_CRT_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 인증서정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_CRT_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 인증서정보 수정
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_CRT_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 인증서정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_CRT_D1000J(Map<String, Object> param) throws DefinedException;

	/**
	 * Agent Master Key 정보 조회
	 * @param param
	 * @return
	 * @throws DefinedException
	 */
	String AGT_CRT_S1001J(Map<String, Object> param) throws DefinedException;

	/**
	 * Agent Master Key 추가
	 * @param param
	 */
	void AGT_CRT_U1001J(Map<String, Object> param) throws DefinedException;

}
