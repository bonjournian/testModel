package cn.rf.hz.web.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TestJSON {

	private static Logger LOG = null;

	@BeforeClass
	public void beforeClass() {
		LOG = Logger.getLogger(TestJSON.class);
		System.out.println("=======This is BeforeClass=======");
	}

	/**
	 * 构造请求接口所需要的参数
	 * 
	 * @return
	 */
	@DataProvider(name = "params")
	public Object[][] params() {
		Object[][] result = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql1 = "select carParkId,recognitionNumber from gd_entrance_recognize where inTime between '2016-07-29' and '2016-08-01' limit 0,3";
		// String sql2 =
		// "select carParkId,recognitionNumber from gd_entrance_recognize where inTime between '2016-06-29' and '2016-07-01' limit 0,2";
		// String sql3 =
		// "select carParkId,recognitionNumber from gd_entrance_recognize where inTime between '2016-05-29' and '2016-06-01' limit 0,2";
		// String sql4 =
		// "select carParkId,recognitionNumber from gd_entrance_recognize where inTime between '2016-04-29' and '2016-05-01' limit 0,2";
		// String sql5 =
		// "select carParkId,recognitionNumber from gd_entrance_recognize where inTime between '2016-03-29' and '2016-04-01' limit 0,2";
		try {
			conn = JDBCUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql1);
			// st.addBatch(sql1);
			// st.addBatch(sql2);
			// st.addBatch(sql3);
			// st.addBatch(sql4);
			// st.addBatch(sql5);
			// st.executeBatch();

			// st.clearBatch();
			rs.last();
			result = new Object[rs.getRow()][];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Object[8];
			}
			rs.first();
			result[0][0] = rs.getInt("carParkId");
			result[0][1] = rs.getString("recognitionNumber");
			result[0][2] = "";//rs.getString("userID");
			result[0][3] = "";//rs.getString("appId");
			result[0][4] = "";//rs.getString("userType");
			result[0][5] = "";//rs.getString("urlType");
			result[0][6] = "";//rs.getString("src");
			result[0][7] = "";//rs.getString("userPhoneId");
			int i = 1;
			while(rs.next()) {
				result[i][0] = rs.getInt("carParkId");
				result[i][1] = rs.getString("recognitionNumber");
				result[i][2] = "";//rs.getString("userID");
				result[i][3] = "";//rs.getString("appId");
				result[i][4] = "";//rs.getString("userType");
				result[i][5] = "";//rs.getString("urlType");
				result[i][6] = "";//rs.getString("src");
				result[i][7] = "";//rs.getString("userPhoneId");
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.release(rs, st, conn);
		}
		return result;
	}

	public String selectNewTest(int pid, String search, String userID,
			String appId, String userType, String urlType, String src,
			String userPhoneId) {
		String desJson = null;
		// 构建URI
		URI uri = constructNewGetURI(pid, search, userID, appId, userType,
				urlType, src, userPhoneId);
		if (uri != null) {
			desJson = returnJson(uri);
		}
		return desJson;
	}

	public String selectOldTest(int pid, String search, String userID,
			String appId, String userType, String urlType, String src,
			String userPhoneId) {
		String origin = null;
		// 构建URI
		URI uri = constructOldGetURI(pid, search, userID, appId, userType,
				urlType, src, userPhoneId);
		if (uri != null) {
			origin = returnJson(uri);
		}
		return origin;
	}

	/**
	 * 返回Json数据
	 * 
	 * @param uri
	 * @return
	 */
	private String returnJson(URI uri) {
		String desJson = null;
		// 构建 httpClient
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		// 设置超时时间
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000).setConnectionRequestTimeout(2000)
				.build();
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		String returnStr = null;
		try {
			response = httpClient.execute(httpGet);
			returnStr = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			LOG.error("测试-请求错误：" + e);
		}
		// 解析Json
		try {
			desJson = URLDecoder.decode(returnStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("测试-解码响应数据失败：" + e);
		}
		return desJson;
	}

	/**
	 * 测试
	 * 
	 * @param pid
	 * @param search
	 * @param userID
	 * @param appId
	 * @param userType
	 * @param urlType
	 * @param src
	 * @param userPhoneId
	 * @return
	 */
	@Test(dataProvider = "params")
	public void judgeJson(int pid, String search, String userID, String appId,
			String userType, String urlType, String src, String userPhoneId) {
		String origin = selectOldTest(pid, search, userID, appId, userType,
				urlType, src, userPhoneId);
		String des = selectNewTest(pid, search, userID, appId, userType,
				urlType, src, userPhoneId);
		LOG.info(parseJson(des, origin));
	}

	/**
	 * 构建重构所需的URI
	 * 
	 * @param pid
	 * @param search
	 * @param userID
	 * @param appId
	 * @param userType
	 * @param urlType
	 * @param src
	 * @param userPhoneId
	 * @return
	 */
	public URI constructNewGetURI(int pid, String search, String userID,
			String appId, String userType, String urlType, String src,
			String userPhoneId) {
		URI uri = null;
		URIBuilder uriBuilder = new URIBuilder();
		// "192.168.0.85"
		uriBuilder.setScheme("http").setHost("192.168.0.85").setPort(8080);
		uriBuilder.setPath("/carPark/p/newPay");
		uriBuilder.setParameter("pid", pid + "");
		uriBuilder.setParameter("search", search);
		if (appId != null && appId != "") {
			uriBuilder.setParameter("appId", appId);
		}
		if (userID != null && userID != "") {
			uriBuilder.setParameter("userID", userID);
		}
		if (userType != null && userType != "") {
			uriBuilder.setParameter("userType", userType);
		}
		if (urlType != null && urlType != "") {
			uriBuilder.setParameter("urlType", urlType);
		}
		if (src != null && src != "") {
			uriBuilder.setParameter("src", src);
		}
		if (userPhoneId != null && userPhoneId != "") {
			uriBuilder.setParameter("userPhoneId", userPhoneId);
		}
		try {
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			uri = null;
			LOG.error("测试-构建URI错误：" + e);
		}
		return uri;
	}

	/**
	 * 构造现网所需要的URI
	 * 
	 * @param pid
	 * @param search
	 * @param userID
	 * @param appId
	 * @param userType
	 * @param urlType
	 * @param src
	 * @param userPhoneId
	 * @return
	 */
	public URI constructOldGetURI(int pid, String search, String userID,
			String appId, String userType, String urlType, String src,
			String userPhoneId) {
		URI uri = null;
		URIBuilder uriBuilder = new URIBuilder();
		// "192.168.0.85"
		uriBuilder.setScheme("http").setHost("192.168.255.2").setPort(9090);
		uriBuilder.setPath("/carPark/p/newPay");
		uriBuilder.setParameter("pid", pid + "");
		uriBuilder.setParameter("search", search);
		if (appId != null && appId != "") {
			uriBuilder.setParameter("appId", appId);
		}
		if (userID != null && userID != "") {
			uriBuilder.setParameter("userID", userID);
		}
		if (userType != null && userType != "") {
			uriBuilder.setParameter("userType", userType);
		}
		if (urlType != null && urlType != "") {
			uriBuilder.setParameter("urlType", urlType);
		}
		if (src != null && src != "") {
			uriBuilder.setParameter("src", src);
		}
		if (userPhoneId != null && userPhoneId != "") {
			uriBuilder.setParameter("userPhoneId", userPhoneId);
		}
		try {
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			uri = null;
			LOG.error("测试-构建URI错误：" + e);
		}
		return uri;
	}

	/**
	 * 比较现网返回的Json和重构返回的Json是否相同
	 * 
	 * @param str
	 * @param origin
	 * @return
	 */
	public boolean parseJson(String str, String origin) {
		try {
			// 能转
			Object obj = JSON.parse(str);
			Object originObj = JSON.parse(origin);
			if (obj instanceof JSONObject) {
				JSONObject jsonObj = (JSONObject) obj;
				Set<String> jsonObjKeys = jsonObj.keySet();
				Iterator<String> jsonObjKeysIter = jsonObjKeys.iterator();
				while (jsonObjKeysIter.hasNext()) {
					String key = jsonObjKeysIter.next();
					System.out.print(key + ":");
					if (((JSONObject) originObj).containsKey(key)) {
						parseJson(jsonObj.getString(key),
								((JSONObject) originObj).getString(key));
					} else {
						return false;
					}
					System.out.println();
				}
			} else if (obj instanceof JSONArray) {
				JSONArray jsonArr = (JSONArray) obj;
				for (int i = 0; i < jsonArr.size(); i++) {
					parseJson(jsonArr.getString(i),
							((JSONArray) originObj).getString(i));
				}
			} else {
				if (!str.equals(origin)) {
					return false;
				}
				System.out.print(str + ",");
			}

		} catch (Exception e) {
			if (!str.equals(origin)) {
				return false;
			}
			System.out.print(str + ",");
		}
		return true;
	}

	@AfterClass
	public void afterClass() {
		LOG.info("AfterClass");
		System.out.println("=======This is AfterClass=======");
	}

}
