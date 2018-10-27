package com.translate.manga.persistance;


import java.io.Serializable;

public class Return<T> implements Serializable {

    private  T results;
    private String error_code;
    private String error_message;
    private   boolean success=true;

    public Return(){}

    public Return(T results,String error_code,String error_message, boolean success){
        this.results=results;
        this.error_code=error_code;
        this.error_message=error_message;
        this.success=success;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Return<T> result(T elements){
        setResults(elements);
        return this;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

}
