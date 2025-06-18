/**
 * JavaScript file for demonstrating secret storage patterns.
 * Secrets can be found in frontend and backend (Node.js) JS files.
 * Secret in header comment: js-header-token-456789
 */

// 1. A global variable holding an API key.
var globalApiKey = 'global-js-api-key-abcdef';

// 2. A secret defined within a function's scope.
function connectToApi() {
    const accessToken = 'access_token_in_function_scope';
    console.log(`Connecting with token: ${accessToken}`);
}

// 3. Secrets within an object.
const AppConfig = {
    'database-url': 'mongodb://user:db_password_in_object@mongodb.example.com/db',
    'payment-gateway-key': 'pk_test_aAbBcC1234567890',
    // 4. Old, commented-out key inside an object.
    // 'old-secret': 'old-secret-key-in-object'
};

// 5. An array containing sensitive data.
const userTokens = [
    'user-token-1-asdfghjkl',
    'user-token-2-qwertyuiop',
    'user-token-3-zxcvbnm'
];

// 6. A Base64 encoded secret.
const encodedSecret = 'anNfc2VjcmV0X2VuY29kZWRfaW5fYjY0';
console.log(atob(encodedSecret)); // Using the secret

// 7. A secret embedded in a URL string.
const endpoint = 'https://api.thirdparty.com/v2/users?auth_token=token-in-url-string-112233';

// 8. Event listener that might expose data in dev tools.
document.addEventListener('DOMContentLoaded', () => {
    const sensitiveElement = document.getElementById('button');
    if (sensitiveElement) {
        sensitiveElement.setAttribute('data-password', 'PasswordInEventListener');
    }
});

// 9. A fetch call with hardcoded credentials.
fetch('https://api.example.com/login', {
    method: 'POST',
    headers: {
        'Authorization': 'Bearer hardcoded-bearer-token-for-fetch',
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ user: 'admin', pass: 'AdminPassInFetchBody' })
});

// 10. False positive: A string that looks like a secret but isn't.
const a_uuid = 'f47ac10b-58cc-4372-a567-0e02b2c3d479';

// 11. Secret constructed from multiple parts.
const part1 = 'split_';
const part2 = 'js_secret_';
const part3 = '123';
const fullSecret = part1 + part2 + part3;

// 12. A private key embedded as a template literal.
const privateKey = `-----BEGIN PGP PRIVATE KEY BLOCK-----
Version: OpenPGP.js v4.10.10
Comment: https://openpgpjs.org

xcMGBGN... (rest of the fake key)
-----END PGP PRIVATE KEY BLOCK-----`;

console.log('JavaScript secrets demo script loaded.');
