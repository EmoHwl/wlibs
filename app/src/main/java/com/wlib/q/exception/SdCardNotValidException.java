package com.wlib.q.exception;
/**
 * @author weiliang
 * 2015年10月30日
 * @说明:
 */
public class SdCardNotValidException extends Exception {
	private static final long serialVersionUID = -5499911517665209427L;

	public SdCardNotValidException() {}
	
	public SdCardNotValidException(String message) {
		super(message);
	}
	
	public SdCardNotValidException(Throwable throwable) {
		super(throwable);
	}
	
	public SdCardNotValidException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
