const fs = require('fs');
const path = require('path');

const configPath = path.join(__dirname, '../config.json');
const envDir = path.join(__dirname, 'src/environments');
const envPath = path.join(envDir, 'environment.ts');

try {
  const configFile = fs.readFileSync(configPath, 'utf8');
  const config = JSON.parse(configFile);
  
  const licenseKey = config.license || ''; 

  if (!fs.existsSync(envDir)) {
    fs.mkdirSync(envDir, { recursive: true });
  }

  const envConfigFile = `export const environment = {
                          production: false,
                          license: '${licenseKey}'
                        };
`;

  fs.writeFileSync(envPath, envConfigFile, 'utf8');
  console.log(`\x1b[32m%s\x1b[0m`, `environment.ts generato con successo con la chiave PrimeUI!`);

} catch (error) {
  console.error(`\x1b[31m%s\x1b[0m`, `Errore durante la generazione dell'environment.ts:`, error.message);
  process.exit(1);
}
