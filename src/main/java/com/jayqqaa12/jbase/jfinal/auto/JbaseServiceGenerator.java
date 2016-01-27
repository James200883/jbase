/**
 * Copyright (c) 2011-2013, jayqqaa12 12shu (476335667@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayqqaa12.jbase.jfinal.auto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JbaseServiceGenerator {

	protected String packageTemplate = "package %s;%n%n";
	protected String importTemplate = 
			"import com.jayqqaa12.jbase.jfinal.auto.BaseService;%n"
			+ "" + "import %s.%s;%n%n";
	
	
	protected String classDefineTemplate = 
			"/**%n" 
	 + " * Generated by Jbase.%n" + " */%n"
	+ "public class %s extends BaseService<%s> {%n";
	protected String serviceTemplate = "\tpublic static final %s service = (%s)new %s().setDao(%s.dao);%n";

	protected String servicePackageName;
	protected String modelPackageName;
	protected String serviceOutputDir;

	public JbaseServiceGenerator(String servicePackageName, String modelPackageName, String serviceOutputDir) {
		if (StrKit.isBlank(servicePackageName)) throw new IllegalArgumentException("modelPackageName can not be blank.");
		if (servicePackageName.contains("/") || servicePackageName.contains("\\")) throw new IllegalArgumentException(
				"modelPackageName error : " + servicePackageName);
		if (StrKit.isBlank(modelPackageName)) throw new IllegalArgumentException(
				"baseModelPackageName can not be blank.");
		if (modelPackageName.contains("/") || modelPackageName.contains("\\")) throw new IllegalArgumentException(
				"baseModelPackageName error : " + modelPackageName);
		if (StrKit.isBlank(serviceOutputDir)) throw new IllegalArgumentException("modelOutputDir can not be blank.");

		this.servicePackageName = servicePackageName;
		this.modelPackageName = modelPackageName;
		this.serviceOutputDir = serviceOutputDir;
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate service ...");
		for (TableMeta tableMeta : tableMetas)
			genService(tableMeta);
		wirtToFile(tableMetas);
	}

	protected void genService(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genPackage(tableMeta,ret);
		genImport(tableMeta, ret);
		genClassDefine(tableMeta, ret);
		genInstace(tableMeta, ret);
		ret.append(String.format("}%n"));
		tableMeta.modelContent = ret.toString();
	}

	protected void genPackage(TableMeta tableMeta,StringBuilder ret) {
		ret.append(String.format(packageTemplate, JbaseGenerator.getPk(tableMeta, servicePackageName)));
	}

	protected void genImport(TableMeta tableMeta, StringBuilder ret) {
		
		ret.append(String.format(importTemplate, JbaseGenerator.getPk(tableMeta, modelPackageName), tableMeta.modelName));
	}

	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String
				.format(classDefineTemplate, tableMeta.modelName+"Service",   tableMeta.modelName));
	}

	protected void genInstace(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(serviceTemplate, tableMeta.modelName+"Service",tableMeta.modelName+"Service", tableMeta.modelName+"Service" ,tableMeta.modelName));
	}

	protected void wirtToFile(List<TableMeta> tableMetas) {
		try {
			for (TableMeta tableMeta : tableMetas)
				wirtToFile(tableMeta);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 若 model 文件存在，则不生成，以免覆盖用户手写的代码
	 */
	protected void wirtToFile(TableMeta tableMeta) throws IOException {
		
		String pre =tableMeta.name.toLowerCase().replace("_", "").replace(tableMeta.modelName.toLowerCase(), "");
		
		String outDir =serviceOutputDir;
		if(StrKit.notBlank(pre)){
		 outDir = serviceOutputDir + File.separator + pre;
		}
		
		File dir = new File(outDir);
		if (!dir.exists()) dir.mkdirs();
		String target = outDir + File.separator + tableMeta.modelName+"Service" + ".java";

		File file = new File(target);
		if (file.exists()) {
			return; // 若 serivce 存在，不覆盖
		}

		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		} finally {
			fw.close();
		}
	}
}