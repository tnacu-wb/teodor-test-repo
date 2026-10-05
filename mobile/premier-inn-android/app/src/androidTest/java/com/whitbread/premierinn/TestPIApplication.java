package com.whitbread.premierinn;

/*public class TestPIApplication extends BaseApplication {

    @Override
  public void initSdks() {

        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .persistenceModule(new PersistenceModule() {
                    @Override
                    public SharedPreferences provideSecureSharedPreferences(@NonNull Context context) {
                        return new InMemorySharedPreferences();
                    }

                    @Override
                    public FirebaseRemoteConfig provideFirebaseRemoteConfig(@NonNull Context context) {
                        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
                        remoteConfig.setDefaultsAsync(new HashMap<String, Object>() {{
                            put(ContentManagedResourceRepository.Key.TOP_DESTINATIONS.getValue(),
                                    context.getString(R.string.top_destinations));
                            put(ContentManagedResourceRepository.Key.PASSWORD_VALIDATOR.getValue(),
                                    context.getString(R.string.password_validator));
                            put(ContentManagedResourceRepository.Key.CHECK_IN_CHECK_OUT_TIME.getValue(),
                                    context.getString(R.string.checkin_checkout_info_android));
                        }});
                        return remoteConfig;
                    }
                })
                .variantModule(new VariantModule() {
                    @Override
                    public AuthenticationRepository provideAuthenticationRepository(@NonNull AppConfiguration appConfiguration,
                                                                                    @NonNull AuthenticationAPIClient client,
                                                                                    @NonNull ErrorLogger logger,
                                                                                    @NonNull SecureCredentialsManager manager) {
                        return new AuthenticationRepository() {
                            @NotNull
                            @Override
                            public Completable authenticate(@NotNull String user, @NotNull String password, @NonNull UserType userType) {
                                return Completable.complete();
                            }

                            @NotNull
                            @Override
                            public Single<String> renewRefreshToken() {
                                return Single.just("token");
                            }

                            @NotNull
                            @Override
                            public Completable logout() {
                                return Completable.complete();
                            }

                            @Override
                            public boolean isTokenValid() {
                                return true;
                            }

                            @NotNull
                            @Override
                            public Single<String> getIdToken() {
                                return Single.just("Token");
                            }

                            @NotNull
                            @Override
                            public Single<String> getRefreshToken() {
                                return Single.just("Refresh Token");
                            }
                        };
                    }

                    @Override
                    public AppConfiguration provideConfiguration(@NonNull SharedPreferences preferences, IsFeatureOn isFeatureOn) {
                        return new StagingConfiguration(preferences, isFeatureOn) {

                            @Override
                            public String getSnowdropUrl() {
                                return RESTMockServer.getUrl();
                            }

                            @Override
                            public String getMicroServicesUrl() {
                                return RESTMockServer.getUrl();
                            }

                            @Override
                            public String getCheckInOnlineUrl() {
                                return Urls.UAT_CHECK_IN_ONLINE_URL;
                            }
                        };
                    }

                    @Override
                    public OkHttpClient provideOkHttpClient(@NonNull Context context, @NonNull AppConfiguration configuration,
                                                            @NonNull ContentManagedResourceRepository resourceRepository,
                                                            @NonNull AppPackageDetails appPackageDetails) {

                        OkHttpClient okHttpClient = new StagingOkHttpClientBuilder(context, configuration, resourceRepository,
                                appPackageDetails, ConsumerType.NON_RX).build();
                        OkHttp3IdlingResource okHttpIdlingResource = OkHttp3IdlingResource.create("OkHttp", okHttpClient);
                        IdlingRegistry.getInstance().register(okHttpIdlingResource);
                        return okHttpClient;
                    }
                })
                .serviceModule(new ServiceModule())
                .build();

        ComponentsManager.getInstance().setAppComponent(applicationComponent);
    }

}*/
