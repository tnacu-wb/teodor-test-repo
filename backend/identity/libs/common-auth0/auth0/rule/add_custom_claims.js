function (user, context, callback) {
  var namespace = configuration.CCUI_NAMESPACE;
  if (user.roles) {
    const assignedRoles = user.roles;

    if (context.idToken) {
      const idTokenClaims = context.idToken;
      idTokenClaims[`${namespace}/roles`] = assignedRoles;
      context.idToken = idTokenClaims;
    }

    if (context.accessToken) {
      const accessTokenClaims = context.accessToken;
      accessTokenClaims[`${namespace}/roles`] = assignedRoles;
      context.accessToken = accessTokenClaims;
    }
  }
  callback(null, user, context);
}