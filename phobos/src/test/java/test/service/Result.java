package test.service;

/**
 * Created by lo on 1/16/17.
 */
public class Result<T> {
    private boolean ok;
    private String errmsg;
    T result;

    private Result() {}

    public Result(T result) {
        this.ok = true;
        this.result = result;
    }

    public static Result error(String errmsg) {
        Result result = new Result();
        result.ok = false;
        result.errmsg = errmsg;
        return result;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String error) {
        this.errmsg = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
