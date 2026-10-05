import * as fs from 'fs';
import * as path from 'path';
import * as glob from 'glob';
import * as ts from 'typescript';

const projectRoot = path.resolve(__dirname, '../'); // Adjust this to point to your project's root directory

export async function countEndpoints() {
  // Find all base-service.ts files
  const files = glob.sync(`${projectRoot}/**/base-service.ts`);
  let totalEndpointCount = 0;

  files.forEach((file) => {
    const content = fs.readFileSync(file, 'utf-8');
    const sourceFile = ts.createSourceFile(file, content, ts.ScriptTarget.Latest);

    let fileEndpointCount = 0;

    // Traverse the AST to locate `endpoints` object
    const visit = (node: ts.Node) => {
      if (
        ts.isVariableDeclaration(node) &&
        node.name.getText(sourceFile) === 'endpoints' && // Check for "endpoints"
        node.initializer &&
        ts.isObjectLiteralExpression(node.initializer) // Ensure it's an object
      ) {
        // Count the number of keys inside the "endpoints" object
        fileEndpointCount = node.initializer.properties.length;
      }
      ts.forEachChild(node, visit);
    };

    ts.forEachChild(sourceFile, visit);

    // Log and increment the total count
    // if (fileEndpointCount > 0) {
    //   console.log(`${fileEndpointCount} endpoint(s) found in: ${file}`);
    // }
    totalEndpointCount += fileEndpointCount;
  });

  console.log(`Total number of endpoints across the project: ${totalEndpointCount}`);
}

countEndpoints().catch((err) => console.error(err));

async function main() {
  try {
    await countEndpoints(); // Run the function
  } catch (error) {
    console.error('An error occurred while counting endpoints:', error);
  }
}
