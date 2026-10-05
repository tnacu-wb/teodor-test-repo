const path = require("path");
const resolvePath = (relativePath, filePath) => {
  const isRelativePath = relativePath
    .split("/")
    .some((p) => p === "." || p === "..");

  if (isRelativePath) {
    const fileParts = filePath.split("/");
    const pathParts = fileParts
      .slice(0, fileParts.length - 1)
      .concat(relativePath.split("/"));
    return path.resolve(...pathParts);
  }

  return relativePath;
};

const replaceImport = (importsMap, currentImport) => {
  // All components now come from molecules
  if (currentImport.indexOf("~components") !== -1) {
    return `@whitbread-eos/molecules`;
  }

  if (currentImport.indexOf("~utils") !== -1) {
    return `@whitbread-eos/utils`;
  }

  const mapping = importsMap[currentImport];

  return mapping;
};
module.exports = function transformer(file, api, options) {
  const { importsMap } = options;
  const j = api.jscodeshift;
  const rootSource = j(file.source);
  // Identify all import declarations and make sure their
  // source matches the new atomic design structure.
  return j(file.source)
    .find(j.ImportDeclaration)
    .forEach((path) => {
      const existingSrc = path.get("source");
      // const dependencyPath = resolvePath(existingSrc.node.value, file.path);
      const mapping = replaceImport(importsMap, existingSrc.node.value);
      if (mapping) {
        j(existingSrc).replaceWith(j.stringLiteral(mapping));
      }
    })
    .toSource();
};

// use the flow parser
module.exports.parser = "tsx";
