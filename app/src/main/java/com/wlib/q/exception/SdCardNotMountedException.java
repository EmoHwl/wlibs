package com.wlib.q.exception;
/**
 * @author weiliang
 * 2015年10月30日
 * @说明:
 */
public class SdCardNotMountedException extends Exception {
	private static final long serialVersionUID = -7194616733196800372L;

	public SdCardNotMountedException() {}

	public SdCardNotMountedException(String message) {
		super(message);
	}

	public SdCardNotMountedException(Throwable throwable) {
		super(throwable);
	}

	public SdCardNotMountedException(String message, Throwable throwable) {
		super(message, throwable);
	}
}