package com.lvmama.comm.utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedSeckillUtil {
	private static Object SECKILL_LOCK = new Object();
	private final static  Log log = LogFactory.getLog(MemcachedSeckillUtil.class);
	private Properties properties;
	public final static int ONE_HOUR=3600;
	public final static int TWO_HOUR=7200;
	public final static String memcacheMapKey="MEMACACHE_MAP_KEY";
	
	private static MemcachedSeckillUtil instance;
	private static MemCachedClient secKillMemCachedClient;
	
	private void initSecKillMemCached() {
		try {
			properties = new Properties();
			Resource resource=new ClassPathResource("memcached.properties");
			properties.load(resource.getInputStream());
			//Session缓存服务器，“,”表示配置多个memcached服务
			String[] secondkillServers = properties.getProperty("secondkill.cache.server").replaceAll(" ", "").split(",");
			SockIOPool sessionPool = SockIOPool.getInstance("secondkillCacheServer");
			sessionPool.setServers(secondkillServers);
			sessionPool.setFailover(true);
			sessionPool.setInitConn(10);
			sessionPool.setMinConn(5);
			sessionPool.setMaxConn(50);
			sessionPool.setMaintSleep(30);
			sessionPool.setNagle(false);
			sessionPool.setSocketTO(30000);
			sessionPool.setBufferSize(1024*1024*5);
			sessionPool.setAliveCheck(true);
			sessionPool.initialize(); /* 建立MemcachedClient实例 */
			secKillMemCachedClient = new MemCachedClient("secondkillCacheServer");
			secKillMemCachedClient.setPrimitiveAsString(true);//秒杀设置String
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception ex) {
			log.error(ex, ex);
		}
	}
	
	private MemcachedSeckillUtil(){
		initSecKillMemCached();
	}
	
	private boolean isCacheEnabled() {
		boolean useCache = false;
		try {
			useCache = Boolean.valueOf(properties.getProperty("cache.enable"));
		} catch (Exception e) {
			useCache = false;
			log.info("Please enable memcached");
		}
		return useCache;
	}
	
	/**
	 * 改用嵌套类静态实始化
	 * @return
	 */
	public static MemcachedSeckillUtil getInstance() {
		if(instance == null){
			synchronized(SECKILL_LOCK) {
				if (instance==null) {
					instance=new MemcachedSeckillUtil();
				}
			}
		}
		return instance;
	}
	
	public static MemCachedClient getMemCachedSeckillClient() {
		if(secKillMemCachedClient == null){
			synchronized(SECKILL_LOCK) {
				if (secKillMemCachedClient==null) {
					instance = new MemcachedSeckillUtil();
				}
			}
		}
		return secKillMemCachedClient;
	}
	
	/**
	 * 替换
	 * @param key
	 * @param seconds 过期秒数
	 * @param obj
	 * @return
	 */
	public boolean replace(String key, int seconds, Object obj) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		if(obj==null){
			return true;
		}
		try{
			if (isCacheEnabled()) {
				Date  expDate = getDateAfter(seconds);
				boolean result = getMemCachedSeckillClient().replace(key, obj, expDate);
				log.debug("SET A OBJECT: KEY:" + key + ", OBJ:" + obj + ", exp:" + expDate  + ", result:" + result);
				return result;
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e, e);
		}
		return false;
	}

	/**
	 * 放
	 * @param key
	 * @param seconds 过期秒数
	 * @param obj
	 * @return
	 */
	public boolean set(String key, int seconds, Object obj) {
		return set(key, getDateAfter(seconds), obj);
	}

	
	/**
	 * 将KEY保存到memcache中
	 * 
	 * @param key
	 * @param exp
	 * @param obj
	 * @return
	 */
	public boolean set(String key,Date exp,Object obj){
		if(StringUtils.isEmpty(key)){
			return false;
		}
		if(obj==null){
			return true;
		}
		try{
			if (isCacheEnabled()) {
				boolean result = getMemCachedSeckillClient().set(key, obj, exp);
				log.debug("SET A OBJECT: KEY:" + key + ", OBJ:" + obj + ", exp:" + exp + ", result:" + result);
				return result;
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e, e);
		}
		return false;
	}
	
	/**
	 * 把相应的Key值和描述存到此方法中；
	 * 
	 * @param key
	 * @param discript
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean putKeyDisMap(String key,String discript) {
		Map<String,String> memMap ;
		Object obj = getMemCachedSeckillClient().get(memcacheMapKey);
		if(obj == null) {
			memMap = new HashMap<String, String>();
		} else {
			memMap = (HashMap<String, String>) obj;
		}
		memMap.put(key, discript);
		getMemCachedSeckillClient().set(memcacheMapKey,memMap,getDateAfter(60*60*48));
		
		return true;
	}
	
	/**
	 * 放
	 * @param key
	 * @param obj
	 * @return
	 */
	public boolean set(String key, Object obj) {
		return set(key,ONE_HOUR,obj);
	}
	
	/**
	 * 取
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		try{
			if (isCacheEnabled()) {
				Object obj = getMemCachedSeckillClient().get(key);
				log.debug("GET A OBJECT: KEY:" + key + " OBJ:" + obj) ;
				return obj;
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e, e);
		}
		return null;
	}
	
	/**
	 * 清除
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		if(StringUtils.isEmpty(key)){
			return false;
		}
		try{
			if (isCacheEnabled()) {
				log.info("delete memcached key: " + key);
				return getMemCachedSeckillClient().delete(key);
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e, e);
		}
		return true;
	}
	
    /**
     * 获得当前开始活参数秒的时间日期
    * @Title: getDateAfter
    * @Description:
    * @param
    * @return Date    返回类型
    * @throws
     */
    public static Date getDateAfter(int second) {
        Calendar cal = Calendar.getInstance();
 		cal.add(Calendar.SECOND, second);
 		return cal.getTime();
 	}
}
