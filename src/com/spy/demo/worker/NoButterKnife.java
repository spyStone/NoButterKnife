package com.spy.demo.worker;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author spy
 * @date 2020-04-19 16:47
 */
public class NoButterKnife {

    private String[] skip = {"gradle", "cpp", ".h", ".cpp", "assets",
            "anim", "drawable", "values", "res",
            ".apk", "idea", "build", "properties", ".bat", "gradlew", "libs", ".txt", ".jks", ".pro", ".cxx"};

    public void setSkip(String[] skip) {
        this.skip = skip;
    }

    private boolean skip(String p) {
        if (null != skip && skip.length > 0) {
            for (String s : skip) {
                if (p.endsWith(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * modify
     *
     * @param path
     */
    public void searchToModify(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (path.endsWith(".java")) {
                doModify(file.getAbsolutePath());
            }
            return;
        }
        File[] files = file.listFiles();
        if (null == files || files.length <= 0) {
            return;
        }
        for (File f : files) {
            if (!f.exists()) {
                continue;
            }
            String absolutePath = f.getAbsolutePath();
            if (!f.exists() || skip(absolutePath)) {
                continue;
            }
            if (absolutePath.toLowerCase().endsWith("adapter")) {// TODO: 2020/4/19 有点复杂
                continue;
            }
            if (f.isDirectory()) {
                searchToModify(absolutePath);
                continue;
            }
            if (!f.getAbsolutePath().endsWith(".java")) {
                continue;
            }
            try {
                BufferedReader bf = new BufferedReader(new FileReader(f));
                String line;
                while (null != (line = bf.readLine())) {
                    LineInfo lineInfo = new LineInfo(line);
                    if (LineInfo.LineType.IMPORT == lineInfo.getLineType()) {
                        doModify(f.getAbsolutePath());
                        bf.close();
                        break;
                    } else {
                        if (line.contains("class")) {
                            bf.close();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error while reading " + absolutePath + ": " + e.getMessage());
            }
        }
    }

    private void checkClassType(ClassInfo classInfo, LineInfo lineInfo) {
        switch (lineInfo.getLineType()) {
            case DefineAdapter:
                classInfo.setClassType(ClassInfo.ClassType.Adapter);
                break;
            case DefineFragment:
                classInfo.setClassType(ClassInfo.ClassType.Fragment);
                break;
            case DefineActivity:
                classInfo.setClassType(ClassInfo.ClassType.Activity);
                break;
        }
    }


    private void checkInsertFindViewMethod(ClassInfo classInfo, LineInfo lineInfo) {
        if (classInfo.isActivity() && !classInfo.hasOnCreateMethod()) {
            classInfo.setHasOnCreateMethod(lineInfo.getLineType() == LineInfo.LineType.DefineOnCreateInActivity);
        }
        if (classInfo.isFragment() && !classInfo.hasOnViewCreated()) {
            classInfo.setHasOnViewCreated(lineInfo.getLineType() == LineInfo.LineType.DefineOnViewCreatedInFragment);
        }
    }

    private void insertImport(StringBuilder fileContent) {
        fileContent.append("import android.os.Bundle;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import android.view.View;\n\n");
    }

    private void insertOnViewCreated(StringBuilder fileContent, String findOnViewContent) {
        fileContent.append("\n\t@Override\n");
        fileContent.append("\tpublic void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {\n");
        fileContent.append("\t\t").append(findOnViewContent).append("\n");
        fileContent.append("\t\tsuper.onViewCreated(view, savedInstanceState);\n");
        fileContent.append("\t}\n\n");
    }

    private void insertOnCreate(StringBuilder fileContent) {
        fileContent.append("\n\tprotected void onCreate(@Nullable Bundle savedInstanceState) {\n");
        fileContent.append("\t\tsuper.onCreate(savedInstanceState);\n");
        fileContent.append("\t\tfindViews();\n");
        fileContent.append("\t}\n\n");
    }

    private String getViewMemberInitLine(String bindViewLine, String declareLine) {
        String id = bindViewLine.substring(bindViewLine.lastIndexOf(".") + 1, bindViewLine.indexOf(")")).trim();
        String memberName = declareLine.replaceFirst("(\\s*|\\t*|public*|private*|protected*|@.*)\\s*\\t*[A-Z]\\w*(\\s|\\t)+", "")
                .replaceAll("(\\s|\\t|;)*", "");

        System.out.println("bindViewLine:" + bindViewLine);
        System.out.println("declareLine:" + declareLine);
        System.out.println("memberName:" + memberName);

        return memberName + " = view.findViewById(R.id." + id + ");";
    }

    private boolean isEmptyString(String string) {
        return null == string || string.length() <= 0;
    }

    class ModifyInfo {
        String filePath = null;
        StringBuffer findViewMethodContent = null;
        ClassInfo classInfo = new ClassInfo();

        public ModifyInfo(String filePath, StringBuffer findViewMethodContent, ClassInfo classInfo) {
            this.filePath = filePath;
            this.findViewMethodContent = findViewMethodContent;
            this.classInfo = classInfo;
        }
    }

    private ModifyInfo readModifyInfo(String filePath) throws Exception {
        StringBuffer findViewMethodContent = null;
        ClassInfo classInfo = new ClassInfo();

        BufferedReader bf = new BufferedReader(new FileReader(new File(filePath)));
        String readLine = null;
        while (null != (readLine = bf.readLine())) {
            LineInfo lineInfo = new LineInfo(readLine);

            if (classInfo.unknownClassType()) {
                checkClassType(classInfo, lineInfo);
            } else if (readLine.matches("\\s*\\w*\\s*class\\s*\\t*.*\\w+Adapter\\s*\\t*extends\\s*\\t*\\w+Adapter.*\\{.*")) {
                // TODO: 2020/4/20 adapter defined in file
            }
            if (classInfo.isAdapter()) {
                // TODO: 2020/4/19 有点复杂,先跳过
                System.out.println("skip adapter:" + filePath);
                return null;
            }
            checkInsertFindViewMethod(classInfo, lineInfo);

            if (LineInfo.LineType.BindView == lineInfo.getLineType()) {
                String declareLine = bf.readLine();
                while (null == declareLine || declareLine.length() <= 0) {
                    declareLine = bf.readLine();
                }
                String viewMemberInitLine = getViewMemberInitLine(readLine, declareLine);
                if (!isEmptyString(viewMemberInitLine)) {
                    if (null == findViewMethodContent) {
                        findViewMethodContent = new StringBuffer();
                    }
                    findViewMethodContent.append("\t\t").append(viewMemberInitLine).append("\n");
                }
            }
        }

        bf.close();
        return new ModifyInfo(filePath, findViewMethodContent, classInfo);
    }

    private void checkToWriteUntilDefinitionEnd(String readLine, BufferedReader bf, StringBuilder newFileContent) throws IOException {
        if (!readLine.trim().endsWith("{")) {
            while (true) {
                readLine = bf.readLine();
                newFileContent.append(readLine).append("\n");
                if (readLine.trim().endsWith("{")) {
                    break;
                }
            }
        }
    }

    private void writeFile(ModifyInfo modifyInfo) throws Exception {
        ClassInfo classInfo = modifyInfo.classInfo;
        if (!classInfo.unknownClassType() && null != modifyInfo.findViewMethodContent) {
            BufferedReader bf = new BufferedReader(new FileReader(new File(modifyInfo.filePath)));
            String readLine = null;
            boolean newImported = false;
            StringBuilder newFileContent = new StringBuilder();
            while (null != (readLine = bf.readLine())) {
                LineInfo lineInfo = new LineInfo(readLine);

                if (readLine.matches("\\s*\\w*\\s*class\\s*\\t*.*\\w+Adapter\\s*\\t*extends\\s*\\t*\\w+Adapter.*\\{.*")) {
                    // TODO: 2020/4/20 adapter defined in file
                }

                if (LineInfo.LineType.IMPORT != lineInfo.getLineType() && LineInfo.LineType.BindView != lineInfo.getLineType()) {
                    newFileContent.append(readLine).append("\n");
                }

                if (!newImported && LineInfo.LineType.IMPORT == lineInfo.getLineType()) {
                    insertImport(newFileContent);
                    newImported = true;
                }

                switch (lineInfo.getLineType()) {
                    case DefineAdapter:
                        break;
                    case DefineFragment:
                        checkToWriteUntilDefinitionEnd(readLine, bf, newFileContent);
                        if (classInfo.hasOnViewCreated()) {
                            // TODO: 2020/4/19 没空写
                        } else {
                            insertOnViewCreated(newFileContent, modifyInfo.findViewMethodContent.toString());
                        }
                        break;
                    case DefineActivity:
                        checkToWriteUntilDefinitionEnd(readLine, bf, newFileContent);

                        if (classInfo.hasOnCreateMethod()) {
                            // TODO: 2020/4/19 没空写
                        } else {
                            insertOnCreate(newFileContent);
                            modifyInfo.findViewMethodContent.insert(0, "\tpublic void findViews() {\n");
                            modifyInfo.findViewMethodContent.append("\t}");
                            newFileContent.append(modifyInfo.findViewMethodContent.toString()
                                    .replaceAll("view.findViewById", "findViewById"))
                                    .append("\n");
                        }
                        break;
                }
            }

            bf.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(modifyInfo.filePath)));
            writer.write(newFileContent.toString());
            writer.flush();
            writer.close();
            System.out.println(modifyInfo.filePath + " has get rid of butterKnife.");
        }
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void doModify(String javaFilePath) {
        final String filePath = javaFilePath;
        executorService.execute(() -> {
            try {
                System.out.println("start to modify " + filePath);
                ModifyInfo modifyInfo = readModifyInfo(filePath);
                if (null == modifyInfo) {
                    System.out.println("failed:" + filePath);
                } else {
                    writeFile(modifyInfo);
                }
            } catch (Exception e) {
                System.out.println("failed:" + filePath);
                e.printStackTrace();
            }
        });
    }

}
