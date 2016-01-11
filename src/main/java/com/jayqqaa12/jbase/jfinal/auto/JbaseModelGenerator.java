package com.jayqqaa12.jbase.jfinal.auto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JbaseModelGenerator extends ModelGenerator {

	protected String importTemplate = "import %s.%s;%n" + "import com.jfinal.ext.plugin.tablebind.TableBind;%n;";

	protected String classDefineTemplate = "/**%n" + " * Generated by Jbase .%n" + " */%n"
			+ "@SuppressWarnings(\"serial\")%n" + "public class %s extends %s<%s> {%n";

	protected String daoTemplate = "\tpublic static final %s dao = new %s();%n";

	public JbaseModelGenerator(String modelPackageName, String baseModelPackageName, String modelOutputDir) {
		super(modelPackageName, baseModelPackageName, modelOutputDir);
	}

	@Override
	protected void genModelContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genPackage(tableMeta, ret);
		genImport(tableMeta, ret);
		genClassDefine(tableMeta, ret);
		genDao(tableMeta, ret);
		ret.append(String.format("}%n"));
		tableMeta.modelContent = ret.toString();
	}

	@Override
	protected void genImport(TableMeta tableMeta, StringBuilder ret) {

		String pk = JbaseGenerator.getPk(tableMeta, baseModelPackageName);

		ret.append(String.format(importTemplate, pk, tableMeta.baseModelName));
	}

	@Override
	protected void genDao(TableMeta tableMeta, StringBuilder ret) {
		if (generateDaoInModel) ret.append(String.format(daoTemplate, tableMeta.modelName, tableMeta.modelName));
		else ret.append(String.format("\t%n"));
	}

	protected void genPackage(TableMeta tableMeta, StringBuilder ret) {
		String pk = JbaseGenerator.getPk(tableMeta, modelPackageName);
		ret.append(String.format(packageTemplate, pk));

	}

	@Override
	protected void wirtToFile(TableMeta tableMeta) throws IOException {

		String pre = tableMeta.name.toLowerCase().replace("_", "").replace(tableMeta.modelName.toLowerCase(), "");

		String outDir = modelOutputDir;
		if (StrKit.notBlank(pre)) {
			outDir = modelOutputDir + File.separator + pre;
		}

		File dir = new File(outDir);
		if (!dir.exists()) dir.mkdirs();

		String target = outDir + File.separator + tableMeta.modelName + ".java";

		File file = new File(target);
		if (file.exists()) {
			return; // 若 Model 存在，不覆盖
		}

		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		} finally {
			fw.close();
		}
	}

	@Override
	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {

		ret.append(String.format(classDefineTemplate, tableMeta.modelName, tableMeta.baseModelName,
				tableMeta.modelName));
	}

}
