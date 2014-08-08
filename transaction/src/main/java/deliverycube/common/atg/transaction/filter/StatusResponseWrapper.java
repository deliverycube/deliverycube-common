package deliverycube.common.atg.transaction.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class StatusResponseWrapper extends HttpServletResponseWrapper {

    private int mStatusCode;

    public StatusResponseWrapper(final HttpServletResponse pResponse) {
        super(pResponse);
    }

    public int getStatus() {
        return mStatusCode;
    }

    @Override
    public void sendError(final int pStatucCode) throws IOException {
        mStatusCode = pStatucCode;
        super.sendError(pStatucCode);
    }

    @Override
    public void sendError(final int pStatusCode, final String pMessage) throws IOException {
        mStatusCode = pStatusCode;
        super.sendError(pStatusCode, pMessage);
    }

    @Override
    public void setStatus(final int pStatusCode) {
        mStatusCode = pStatusCode;
        super.setStatus(pStatusCode);
    }

}