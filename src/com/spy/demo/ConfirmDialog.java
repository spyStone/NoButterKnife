package com.spy.demo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author spy
 * @date 2020-04-19 16:33
 */
public class ConfirmDialog extends DialogWrapper {


    private Project project;
    private String singleFilePath;

    public String getSingleFilePath() {
        return singleFilePath;
    }

    public ConfirmDialog setSingleFilePath(String singleFilePath) {
        this.singleFilePath = singleFilePath;
        return ConfirmDialog.this;
    }

    protected ConfirmDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        setTitle("Confirm to quit butterknife");
        if (null != project) {
            this.project = project;
        }
        init();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (null != singleFilePath) {
            NoButterKnifeService.getInstance(this.project).start(this.singleFilePath);
        } else if (null != this.project) {
            NoButterKnifeService.getInstance(this.project).start(this.project);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel("<html>0. <span style='color:red'>ATTENTION:</span> Your project files will not be backed up by this plugin!<br/>" +
                "1. Please back up your project first.<br/>" +
                "2. Please back up your project first.<br/>" +
                "3. Please back up your project first.<br/>" +
                "4. Click OK to continue the process, then do not change anything.<br/>" +
                "<html>");
        jPanel.add(jLabel);
        return jPanel;
    }


}
