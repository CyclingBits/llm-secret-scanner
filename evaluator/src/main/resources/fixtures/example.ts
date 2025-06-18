/**
 * TypeScript file for demonstrating secret storage patterns.
 * Due to the type system, secrets can be defined in interesting ways.
 * Secret in header comment: ts-header-token-998877
 */

// 1. A typed global variable holding an API key.
const globalApiKey: string = 'global-ts-api-key-ghijkl';

// 2. A secret defined within a typed function's scope.
function connectToApi(user: string): void {
    const accessToken: string = `access_token_for_${user}`;
    console.log(`Connecting with token: ${accessToken}`);
}

// 3. Defining secrets within an interface and class.
interface IAuthConfig {
    readonly jwtSecret: string;
    'payment-gateway-key': string;
}

class AppConfig implements IAuthConfig {
    'database-url' = 'mongodb://user:db_password_in_class@mongodb.example.com/db';
    jwtSecret = 'super_secret_jwt_from_class';
    'payment-gateway-key' = 'pk_live_aAbBcC1234567890';
    // 4. Old, commented-out key
    // private oldSecret = 'old-secret-key-in-class';
}

// 5. An array of sensitive data with a type.
const userTokens: Array<string> = [
    'ts-user-token-1-asdfghjkl',
    'ts-user-token-2-qwertyuiop',
    'ts-user-token-3-zxcvbnm'
];

// 6. A Base64 encoded secret.
const encodedSecret: string = 'dHNfc2VjcmV0X2VuY29kZWRfaW5fYjY0'; // "ts_secret_encoded_in_b64"
console.log(atob(encodedSecret)); // Using the secret in a browser context (atob is globally available)

// 7. A secret embedded in a URL string, assigned to a URL object.
const endpoint: URL = new URL('https://api.thirdparty.com/v2/users?auth_token=token-in-url-object-445566');

// 8. Decorator that contains a secret (advanced and specific to TS).
// The descriptor is made optional to handle different decorator invocation signatures.
function Authorize(secret: string) {
    return function (target: any, propertyKey: string, descriptor?: PropertyDescriptor) {
        console.log(`Method ${propertyKey} is protected with secret: ${secret}`);
        if (descriptor) {
            // This is a method decorator, return the descriptor.
            return descriptor;
        }
        // This could be a property decorator, so there is no descriptor to return.
    };
}

class ApiClient {
    // @ts-ignore
    @Authorize('decorator-secret-key-pqrst')
    fetchSensitiveData() {
        // ... logic
    }
}

// 9. A fetch call with typed headers.
const headers: HeadersInit = {
    'Authorization': 'Bearer hardcoded-bearer-token-for-ts-fetch',
    'Content-Type': 'application/json'
};

fetch('https://api.example.com/login', {
    method: 'POST',
    headers: headers,
    body: JSON.stringify({ user: 'admin_ts', pass: 'AdminPassInTsFetchBody' })
});

// 10. False positive: A type definition that might look suspicious.
type HexColor = `#${string}`;
const brandColor: HexColor = '#a1b2c3';

// 11. Secret constructed from multiple parts with type inference.
const part1 = 'split_';
const part2 = 'ts_secret_';
const part3 = '456';
const fullSecret: string = part1 + part2 + part3;

// 12. A private key embedded in a constant.
const privateKey: `-----BEGIN PGP PRIVATE KEY BLOCK-----${string}` = `-----BEGIN PGP PRIVATE KEY BLOCK-----
Version: OpenPGP.js v5.0.0
Comment: https://openpgpjs.org

xcMGBGN... (rest of the fake key)
-----END PGP PRIVATE KEY BLOCK-----`;

console.log('TypeScript secrets demo script loaded.');