package com.losers.argraphlibrary.Base;

public interface ResponseBaseView {

  void onError(Object object, Object object2);

  void onSuccess(Object object, Object object2);

  void onLoading(String message);

}
