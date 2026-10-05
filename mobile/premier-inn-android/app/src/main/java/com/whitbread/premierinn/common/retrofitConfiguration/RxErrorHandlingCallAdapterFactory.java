package com.whitbread.premierinn.common.retrofitConfiguration;

import com.whitbread.premierinn.data.remote.ApiError;
import com.whitbread.premierinn.data.remote.ApiErrorResponse;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.data.remote.GraphQLErrorBody;
import com.whitbread.premierinn.data.remote.GraphQlThrowable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Custom RxJava2CallAdapterFactory that handles @{@link com.whitbread.premierinn.data.remote.ApiError}
 * parsing and exposes @{@link ApiThrowable} produces as an Error.
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJava2CallAdapterFactory original;

    private RxErrorHandlingCallAdapterFactory() {
        this.original = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // Ask the RxJava2 factory first
        CallAdapter<?, ?> delegate = original.get(returnType, annotations, retrofit);
        if (delegate == null) {
            // Not an Rx type → let Retrofit try the next factory (e.g., coroutines / plain Call)
            return null;
        }
        return new RxCallAdapterWrapper<>(retrofit, delegate);
    }

    private static final class RxCallAdapterWrapper<R, T> implements CallAdapter<R, T> {
        private final Retrofit retrofit;
        private final CallAdapter<R, T> wrapped;

        RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<R, T> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings({"unchecked", "NullableProblems"})
        @Override
        public Object adapt(Call call) {
            Object adaptedCall = wrapped.adapt(call);

            if (adaptedCall instanceof Completable) {
                return ((Completable) adaptedCall).onErrorResumeNext(
                        throwable -> Completable.error(asApiError(throwable)));
            }

            if (adaptedCall instanceof Single) {
                return ((Single) adaptedCall).onErrorResumeNext(
                        throwable -> Single.error(asApiError((Throwable) throwable)));
            }

            if (adaptedCall instanceof Observable) {
                return ((Observable) adaptedCall).onErrorResumeNext(
                        (Function<? super Throwable, ? extends ObservableSource>)
                                throwable -> Observable.error(asApiError(throwable)));
            }

            throw new RuntimeException("Observable Type not supported");
        }

        private ApiThrowable asApiError(Throwable throwable) {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response<?> response = httpException.response();
                if (response != null && response.errorBody() != null) {
                    try {
                        ApiError apiError = (ApiError) retrofit
                                .responseBodyConverter(ApiError.class, new Annotation[0])
                                .convert(response.errorBody());

                        if (apiError == null) {
                            ApiErrorResponse apiErrorResponse = (ApiErrorResponse) retrofit
                                    .responseBodyConverter(ApiErrorResponse.class, new Annotation[0])
                                    .convert(response.errorBody());
                            return new ApiThrowable.Http(response.code(), null, apiErrorResponse);
                        }
                        return new ApiThrowable.Http(response.code(), apiError);
                    } catch (IOException e) {
                        return new ApiThrowable.Http(response.code(), null);
                    }
                }
            }
            if (throwable instanceof IOException) {
                return new ApiThrowable.Network(throwable);
            }

            if (throwable instanceof GraphQlThrowable) {
                return new GraphQlThrowable.GraphQLError(401, new GraphQLErrorBody(401, "Session expired"));
            }
            return new ApiThrowable.Generic(throwable);
        }
    }
}