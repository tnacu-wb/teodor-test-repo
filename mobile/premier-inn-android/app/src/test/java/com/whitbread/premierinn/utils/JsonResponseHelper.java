package com.whitbread.premierinn.utils;

import com.google.common.base.Strings;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.common.utils.FileUtils;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

public final class JsonResponseHelper {

    public static Response<?> responseOk(Class classz, String filePath) {
        return Response.success(InstanceFactory.create(classz, filePath));
    }

    public static Response<?> responseError(int code, String filePath) {
        String body = Strings.isNullOrEmpty(filePath) ? "" : FileUtils.loadFileFromResource(filePath);
        return Response.error(code, ResponseBody.create(MediaType.parse("UTF-8"), body));
    }
}
