package com.jayqqaa12.model.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayqqaa12.jbase.jfinal.ext.model.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.Render;

public class SendJson {
	public int code = 200;
	/**
	 * 错误描述可写 可不写 可集中在 客户端 判断 code 得出
	 */
	public String msg;

	public Map data = new HashMap();

	public SendJson(Model model) {
		if (model != null) this.data = model.getAttrs();

	}

	public static SendJson create() {
		return new SendJson();
	}

	public SendJson(String key, List list) {
		setData(key, list);
	}

	public SendJson() {

	}

	public SendJson(int code) {

		this.code = code;
	}

	public String toJson() {
		if (data!=null&&data.size() == 0) data = null;
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String rst = gson.toJson(this);

		/**
		 * 只有一个 data 这时候就 去掉一层
		 */
		if (data != null && data.size() == 1) {
			JSONObject json = JSONObject.parseObject(rst);
			Object data = json.getJSONObject("data").get("data");
			if (data != null) {
				json.put("data", data);
				rst = json.toJSONString();
			}
		}

		return rst;
	}

	@Override
	public String toString() {
		return toJson();
	}

	public SendJson setData(String key, Model m) {
		if(m==null)return this;
		this.data.put(key, m.getAttrs());

		return this;
	}

	public SendJson setData(String key, Object value) {
		if(value==null)return this;
		this.data.put(key, value);
		return this;
	}

	public SendJson setData(String key, List list) {
		if (list == null) return this;
		List<Map> attr = new ArrayList<Map>();

		for (Object o : list) {
			if (o instanceof Model) attr.add(((Model) o).getAttrs());
			if (o instanceof Record) attr.add(((Record) o).getColumns());

		}
		data.put(key, attr);

		if (attr.size() == 0) data.put(key, list);

		
		return this;
	}

	public void setData(Map<Object, Object> data) {

		for (Object key : data.keySet()) {
			Object o = data.get(key);
			
			if(o instanceof List)  this.setData((String)key,(List)o);
			else if (o instanceof Model) this.data.put(key, ((Model) o).getAttrs());
			else if (o instanceof Record) this.data.put(key, ((Record) o).getColumns());
			else this.data.put(key, o);

		}

	}

}
