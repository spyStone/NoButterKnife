package com.spy.demo.worker;

/**
 * @author spy
 * @date 2020-04-19 17:25
 */
public class LineInfo {

    private String content;
    private LineType lineType;

    public LineInfo() {
    }

    public LineInfo(String content) {
        this.content = content;
        this.updateLineType();
    }

    public void updateLineType() {
        if (null == content || content.length() < 0) {
            lineType = LineType.Muggle;
        } else {
            if (content.matches("(\\w|\\s|\\t)*class(\\w|\\s|\\t)*Adapter.*extends.*")) {
                lineType = LineType.DefineAdapter;
            } else if (content.matches("(\\w|\\s|\\t)*class(\\w|\\s|\\t)*Fragment.*extends.*")) {
                lineType = LineType.DefineFragment;
            } else if (content.matches("(\\w|\\s|\\t)*class(\\w|\\s|\\t)*Activity.*extends.*")) {
                lineType = LineType.DefineActivity;
            } else if (content.matches("(\\w|\\s|\\t)*import(\\w|\\s|\\t)*butterknife.*")) {
                lineType = LineType.IMPORT;
            } else if (content.matches("(\\s|\\t)*@BindView(\\s|\\t)*\\(R\\.id\\.\\w+\\)")) {
                lineType = LineType.BindView;
            } else if (content.matches("(\\s|\\t|\\w)*void(\\s|\\t)*onCreate.*\\(.*Bundle(\\s|\\t)*\\w+\\).*")) {
                lineType = LineType.DefineOnCreateInActivity;
            } else if (content.matches("(\\s|\\t|\\w)*void(\\s|\\t)*onViewCreated.*View(\\s|\\t)*\\w+,.*Bundle(\\s|\\t)*\\w+\\).*")) {
                lineType = LineType.DefineOnViewCreatedInFragment;
            }

            else {
                lineType = LineType.Muggle;
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        updateLineType();
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public static enum LineType {
        Muggle, IMPORT, DefineActivity, DefineFragment, DefineAdapter,
        BindView, DefineOnCreateInActivity, DefineOnViewCreatedInFragment,
    }


}
