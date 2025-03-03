package great.project.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import lombok.extern.slf4j.Slf4j;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

@Slf4j
public class FunctionBraceWhitespaceCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.METHOD_DEF};
    }

    @Override
    public void visitToken(DetailAST ast) {
        // 获取方法的代码块 `{}`
        DetailAST methodBlock = ast.findFirstToken(TokenTypes.SLIST);
        if (methodBlock == null) {
            return; // 如果没有 `{}` 代码块，跳过
        }

        // 获取 `{` 和 `}` 所在的行号
        int startLine = methodBlock.getLineNo();
        DetailAST lastChild = methodBlock.getLastChild();
        int endLine = (lastChild != null) ? lastChild.getLineNo() : startLine;

        boolean emptied = emptyMethod(startLine, endLine);
        if (!emptied) {
            // 检查 `{` 之后是否有空白行
            checkBlankLineAfterOpeningBrace(startLine);

            // 检查 `}` 之前是否有空白行
            checkBlankLineBeforeClosingBrace(endLine);
        }
    }

    private void checkBlankLineAfterOpeningBrace(int startLine) {
        String[] lines = getLines();
        if (startLine < lines.length - 1) { // 确保不会越界
            String lineAfterBrace = lines[startLine].trim();
            if (lineAfterBrace.isEmpty()) {
                log(startLine, "函数的 '{' 之后不能有空白行。", SeverityLevel.ERROR);
            }
        }
    }

    private void checkBlankLineBeforeClosingBrace(int endLine) {
        String[] lines = getLines();
        if (endLine - 2 >= 0 && endLine - 2 < lines.length) { // 确保不会越界

            String lineBeforeBrace = lines[endLine - 2].trim();
            if (lineBeforeBrace.isEmpty()) {
                log(endLine, "函数的 '}' 之前不能有空白行。", SeverityLevel.ERROR);
            }
        }
    }

    public void empty() {

    }

    private boolean emptyMethod(int startLine, int endLine) {
        if (startLine == endLine) {
            return true;
        }
        for (int i = startLine + 1; i < endLine; i++) {
            if (!getLine(i - 1).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
