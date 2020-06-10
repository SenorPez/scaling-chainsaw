module.exports = () => {
    const authData = {
        'grant_type': 'password',
        'client_id': 'api',
        'client_secret': process.env.CLIENT_SECRET,
        'username': 'senorpez',
        'password': process.env.PASSWORD
    }
    const authBody = Object.keys(authData).map(key => encodeURIComponent(key) + '=' + encodeURIComponent(authData[key])).join('&');

    return fetch(`https://www.senorpez.com:8448/auth/realms/loot/protocol/openid-connect/token`, {
        method: "post",
        body: authBody,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.json());
}