package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * Task 할당 Batch구동 정보
 * @author 장수호
 *
 */
public interface AGT_BAT_Service {

	/**
	 * Batch정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_BAT_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * Batch정보 단일 조회
	 * @param param
	 * @throws Exception
	 */
	Map<String,Object> AGT_BAT_V1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * Batch정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_BAT_I1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Batch정보 수정
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_BAT_U1000J(Map<String, Object> param) throws Exception;
	
	/**
	 * Batch구동시간 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_BAT_U1001J(Map<String, Object> param) throws Exception;
	
	/**
	 * Batch정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_BAT_D1000J(Map<String, Object> param) throws Exception;
}
