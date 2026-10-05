package com.whitbread.premierinn.common.retrofitConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
@Deprecated
public class RxCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter callAdapter = null;
        if (getRawType(returnType) == Observable.class) {
            final CallAdapter<Object, Observable<?>> delegate =
                    (CallAdapter<Object, Observable<?>>) retrofit.nextCallAdapter(this, returnType, annotations);

            callAdapter = new CallAdapter<Object, Object>() {

                @Override
                public Type responseType() {
                    return delegate.responseType();
                }

                @Override
                public Object adapt(Call<Object> call) {
                    Observable<?> o = delegate.adapt(call);
                    return o.observeOn(AndroidSchedulers.mainThread());
                }
            };
        } else if (getRawType(returnType) == Single.class) {
            final CallAdapter<Object, Single<?>> delegate =
                    (CallAdapter<Object, Single<?>>) retrofit.nextCallAdapter(this, returnType, annotations);

            callAdapter = new CallAdapter<Object, Object>() {

                @Override
                public Type responseType() {
                    return delegate.responseType();
                }

                @Override
                public Object adapt(Call<Object> call) {
                    Single<?> o = delegate.adapt(call);
                    return o.observeOn(AndroidSchedulers.mainThread());
                }
            };
        }
        return callAdapter;
    }
}
