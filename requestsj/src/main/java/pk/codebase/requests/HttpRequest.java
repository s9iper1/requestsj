/*
 * Requests for Android
 * Copyright (C) 2016-2019 CodeBasePK
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pk.codebase.requests;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequest {
    private final String CONTENT_TYPE_JSON = "application/json";
    private final String CONTENT_TYPE_FORM = String.format(
            "multipart/form-data; boundary=%s", FormData.BOUNDARY);
    private final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    private OnErrorListener mOnErrorListener;
    private OnFileUploadProgressListener mOnFileUploadProgressListener;
    private OnResponseListener mOnResponseListener;

    private ExecutorService mThread;
    private Handler mHandler;
    private final String mBaseURL;

    public HttpRequest() {
        this("");
    }

    public HttpRequest(String baseURL) {
        mHandler = new Handler(Looper.getMainLooper());
        mThread = Executors.newSingleThreadExecutor();
        mBaseURL = baseURL;
    }

    public interface OnErrorListener {
        void onError(HttpError error);
    }

    public interface OnFileUploadProgressListener {
        void onFileUploadProgress(HttpFileUploadProgress progress);
    }

    public interface OnResponseListener {
        void onResponse(HttpResponse response);
    }

    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    public void setOnFileUploadProgressListener(OnFileUploadProgressListener listener) {
        mOnFileUploadProgressListener = listener;
    }

    public void setOnResponseListener(OnResponseListener listener) {
        mOnResponseListener = listener;
    }

    private void actuallyRequest(String method, String rawURL, Object payload, HttpHeaders headers,
                                 HttpOptions options) {
        String url = rawURL;
        if (url != null) {
            if (!url.startsWith("http") && !mBaseURL.isEmpty()) {
                if (mBaseURL.endsWith("/") && url.startsWith("/")) {
                    url = String.format("%s%s", mBaseURL, url.substring(1));
                } else if (!mBaseURL.endsWith("/") && !url.startsWith("/")) {
                    url = String.format("%s/%s", mBaseURL, url);
                } else {
                    url = String.format("%s%s", mBaseURL, url);
                }
            }
        }
        HttpBase http = new HttpBase();
        http.setUploadProgressListener(new OnFileUploadProgressListener() {
            @Override
            public void onFileUploadProgress(HttpFileUploadProgress progress) {
                emitOnFileUploadProgress(progress);
            }
        });
        HttpHeaders actualHeaders = headers;
        if (actualHeaders == null) {
            actualHeaders = new HttpHeaders();
        }
        HttpOptions actualOptions = options;
        if (actualOptions == null) {
            actualOptions = new HttpOptions();
        }
        if (!method.equals("GET")) {
            if (payload instanceof FormData) {
                actualHeaders.put("Content-Type", CONTENT_TYPE_FORM);
            } else if (!actualHeaders.containsKey("Content-Type") ||
                    !actualHeaders.containsKey("content-type")) {
                actualHeaders.put("Content-Type", CONTENT_TYPE_JSON);
            }
        }
        try {
            emitOnResponse(http.request(method, url, payload, actualHeaders, actualOptions));
        } catch (HttpError error) {
            emitOnError(error);
        } catch (Exception e) {
            emitOnError(new HttpError(HttpError.UNKNOWN, HttpError.STAGE_UNKNOWN, e));
        }
    }

    private void request(final String method, final String rawURL, final Object payload,
                         final HttpHeaders headers, final HttpOptions options) {
        mThread.submit(new Runnable() {
            @Override
            public void run() {
                actuallyRequest(method, rawURL, payload, headers, options);
            }
        });
    }

    public void get(String url) {
        get(url, null, null);
    }

    public void get(String url, HttpHeaders headers) {
        get(url, headers, null);
    }

    public void get(String url, HttpOptions options) {
        get(url, null, options);
    }

    public void get(String url, HttpHeaders headers, HttpOptions options) {
        request("GET", url, null, headers, options);
    }

    public void post(String url) {
        post(url, null, null, null);
    }

    public void post(String url, HttpHeaders headers) {
        post(url, null, headers, null);
    }

    public void post(String url, HttpOptions options) {
        post(url, null, null, options);
    }

    public void post(String url, Object payload) {
        post(url, payload, null, null);
    }

    public void post(String url, Object payload, HttpHeaders headers) {
        post(url, payload, headers, null);
    }

    public void post(String url, Object payload, HttpOptions options) {
        post(url, payload, null, options);
    }

    public void post(String url, Object payload, HttpHeaders headers, HttpOptions options) {
        request("POST", url, payload, headers, options);
    }

    public void put(String url) {
        put(url, null, null, null);
    }

    public void put(String url, HttpHeaders headers) {
        put(url, null, headers, null);
    }

    public void put(String url, HttpOptions options) {
        put(url, null, null, options);
    }

    public void put(String url, Object payload) {
        put(url, payload, null, null);
    }

    public void put(String url, Object payload, HttpHeaders headers) {
        put(url, payload, headers, null);
    }

    public void put(String url, Object payload, HttpOptions options) {
        put(url, payload, null, options);
    }

    public void put(String url, Object payload, HttpHeaders headers, HttpOptions options) {
        request("PUT", url, payload, headers, options);
    }

    public void patch(String url) {
        patch(url, null, null, null);
    }

    public void patch(String url, HttpHeaders headers) {
        patch(url, null, headers, null);
    }

    public void patch(String url, HttpOptions options) {
        patch(url, null, null, options);
    }

    public void patch(String url, Object payload) {
        patch(url, payload, null, null);
    }

    public void patch(String url, Object payload, HttpHeaders headers) {
        patch(url, payload, headers, null);
    }

    public void patch(String url, Object payload, HttpOptions options) {
        patch(url, payload, null, options);
    }

    public void patch(String url, Object payload, HttpHeaders headers, HttpOptions options) {
        request("PATCH", url, payload, headers, options);
    }

    public void delete(String url) {
        delete(url, null, null, null);
    }

    public void delete(String url, HttpHeaders headers) {
        delete(url, null, headers, null);
    }

    public void delete(String url, HttpOptions options) {
        delete(url, null, null, options);
    }

    public void delete(String url, Object payload) {
        delete(url, payload, null, null);
    }

    public void delete(String url, Object payload, HttpHeaders headers) {
        delete(url, payload, headers, null);
    }

    public void delete(String url, Object payload, HttpOptions options) {
        delete(url, payload, null, options);
    }

    public void delete(String url, Object payload, HttpHeaders headers, HttpOptions options) {
        request("DELETE", url, payload, headers, options);
    }

    private void emitOnResponse(final HttpResponse response) {
        if (mOnResponseListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnResponseListener.onResponse(response);
                }
            });
        }
    }

    private void emitOnFileUploadProgress(final HttpFileUploadProgress progress) {
        if (mOnFileUploadProgressListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnFileUploadProgressListener.onFileUploadProgress(progress);
                }
            });
        }
    }

    private void emitOnError(final HttpError error) {
        if (mOnErrorListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnErrorListener.onError(error);
                }
            });
        }
    }
}
