# Security policy

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID is at its first steps. It holds the access token of a GitHub Actions Monitor and can approve or reject action proposals, so please report a vulnerability, or anything that could make the app send the token to the wrong place, show or approve something the person did not see, or keep a secret in clear text.

## Reporting a problem

Please report it privately to **electrohobby3d@gmail.com**, with what you found and where. Do not open a public issue for it. You will get an answer as soon as possible; this is a one-person project.

Only the latest version is supported.

## What the app promises

* The token is kept sealed with a key of the Android Keystore, is never written to a log or to an error text and is sent only to the address that was typed; redirects are not followed.
* Plain `http://` is accepted only for localhost, `.local` names and private IPv4 addresses.
* An approval is bound to the hash of the content that was on the screen, and a link only opens a proposal.
* Push notifications are off by default and critical-only; no Firebase file, key or keystore is part of this repository.
