package com.spy.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;

/**
 * @author spy
 * @date 2020-04-20 22:42
 */
public class SingleFileAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (null == psiFile || psiFile.getFileType().isReadOnly()) {
            return;
        }
        String path = psiFile.getViewProvider().getVirtualFile().getPath();
        if(!path.endsWith(".java")){
            return;
        }
        new ConfirmDialog(e.getProject(), true).setSingleFilePath(path).show();
    }
}
