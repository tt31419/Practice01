package com.tt;

import org.apache.commons.lang.StringUtils;

import com.tt.enums.CustErrorCode;

public class CustException extends Exception {

	private static final long serialVersionUID = 6978485190583026069L;
	
	private String exceptionCode = null;

	private String exceptionMsg = null;

	public CustException(Throwable e) {
		super(e);
		if(e!=null) {
			if(e instanceof CustException) {
				this.exceptionCode = ((CustException)e).getExceptionCode();
				this.exceptionMsg = ((CustException)e).getExceptionMsg();
			} else {
				this.exceptionCode = CustErrorCode.ERROR_99999.getCode();
				this.exceptionMsg = CustErrorCode.replaceException(e);
			}
		}
		if(StringUtils.isBlank(this.exceptionCode)) {
			this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
			this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionCode", null);
		} else {
			if(StringUtils.isBlank(this.exceptionMsg)) {
				this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
				this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionMsg", null);
			}
		}
	}
	
	public CustException(String msg, Throwable e) {
		super(msg, e);
		if(e!=null) {
			if(e instanceof CustException) {
				this.exceptionCode = ((CustException)e).getExceptionCode();
				this.exceptionMsg = ((CustException)e).getExceptionMsg();
			} else {
				this.exceptionCode = CustErrorCode.ERROR_99999.getCode();
				this.exceptionMsg = CustErrorCode.ERROR_99999.replaceParameter(null, msg);
			}
		}
		if(StringUtils.isBlank(this.exceptionCode)) {
			this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
			this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionCode", null);
		} else {
			if(StringUtils.isBlank(this.exceptionMsg)) {
				this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
				this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionMsg", null);
			}
		}
	}

	public CustException(String code, String msg) {
		super(msg);
		this.exceptionCode = code;
		this.exceptionMsg = msg;
		if(StringUtils.isBlank(this.exceptionCode)) {
			this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
			this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionCode", null);
		} else {
			if(StringUtils.isBlank(this.exceptionMsg)) {
				this.exceptionCode = CustErrorCode.ERROR_90001.getCode();
				this.exceptionMsg = CustErrorCode.ERROR_90001.replaceParameter("exceptionMsg", null);
			}
		}
	}
	
	public CustException(CustErrorCode codeEnum) {
		this(codeEnum, null, null);
	}
	
	public CustException(CustErrorCode codeEnum, String param) {
		this(codeEnum, param, null);
	}
	
	public CustException(CustErrorCode codeEnum, String param, String value) {
		super();
		if(codeEnum!=null) {
			this.exceptionCode = codeEnum.getCode();
			this.exceptionMsg = codeEnum.replaceParameter(param, value);
		} else {
			this.exceptionCode = CustErrorCode.ERROR_90003.getCode();
			this.exceptionMsg = CustErrorCode.ERROR_90003.replaceParameter("codeEnum", null);
		}
	}

	@Override
	public String getMessage() {
		return ((!StringUtils.isBlank(this.exceptionMsg))?this.exceptionMsg:super.getMessage());
	}
	
	public boolean isByPass() {
		if(StringUtils.isBlank(this.exceptionCode)) {
			return false;
		}
		return CustErrorCode.isByPass(this.exceptionCode);
	}
	
	public boolean isSuccess() {
		if(StringUtils.isBlank(this.exceptionCode)) {
			return false;
		}
		return CustErrorCode.isSuccess(this.exceptionCode);
	}
	
	public boolean isFail() {
		if(StringUtils.isBlank(this.exceptionCode)) {
			return false;
		}
		return CustErrorCode.isFail(this.exceptionCode);
	}
	
	public String getErrorCode() {
		return null;
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	@SuppressWarnings("unused")
	private void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	@SuppressWarnings("unused")
	private void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
}

