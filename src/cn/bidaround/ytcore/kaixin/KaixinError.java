/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bidaround.ytcore.kaixin;

/**
 * 封装服务器返回错误信息的类
 * 
 */
public class KaixinError extends RuntimeException {

	private static final long serialVersionUID = -7614264285563089016L;
	
	/**
	 * 服务器返回的错误信息
	 */
	private int mErrorCode;
	private String mRequest;
	private String mResponse;

	public KaixinError(String errorMessage) {
		super(errorMessage);
	}

	public KaixinError(int errorCode, String errorMessage, String request,
			String response) {
		super(errorMessage);
		mErrorCode = errorCode;
		mRequest = request;
		mResponse = response;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public String getRequest() {
		return mRequest;
	}

	public String getResponse() {
		return mResponse;
	}

	@Override
	public String toString() {
		return "errorCode:" + mErrorCode + "\nerrorMessage:"
				+ this.getMessage() + "\nrequest:" + mRequest + "\nresponse:"
				+ mResponse;
	}
}
