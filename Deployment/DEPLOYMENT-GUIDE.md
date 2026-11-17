# Deployment guide — Railway + cloud MySQL

A real public URL cannot be created from this source bundle alone. The repository owner must authorize GitHub/Railway and provide a cloud database. Follow these steps; only publish the generated Railway domain after testing it.

## A. Prepare the database
1. Create a managed MySQL 8 database with a cloud provider that is reachable from Railway.
2. Run `DBMS-Database/schema.sql` against it. Optionally run `sample-data.sql` once.
3. Copy the provider's host, port, database name, username and password into a secure note. Never commit them.

## B. Push the project to GitHub
```bash
git init
git add .
git commit -m "Upgrade Scenario 12 restaurant management"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
git push -u origin main
```
Ensure `.env` and credentials are not committed.

## C. Create Railway service
1. Sign in to Railway and create a new project from the GitHub repository.
2. Select the repository and root directory. Railway detects the root `Dockerfile`.
3. Add service variables (use the actual cloud database values):
   - `DB_URL` = `jdbc:mysql://HOST:PORT/DATABASE?useSSL=true&serverTimezone=UTC`
   - `DB_USER` = database username
   - `DB_PASSWORD` = database password (secret)
   - `PORT` = `8080` only if the platform does not inject its own port
4. Deploy. The Docker build runs Maven and packages the Spring Boot application.
5. In Railway networking/settings, generate a public domain and enable HTTPS.
6. Open `https://YOUR-GENERATED-DOMAIN/api/health`. It should return JSON with `status` equal to `UP`. Then open the root URL and test a menu/customer/order/payment flow.

If the managed database uses a private Railway network, set `DB_URL` to the private host/service reference in Railway's JDBC URL form. Do not use `localhost` for a cloud database.

## D. Deployment acceptance checklist
- Build logs show Docker image built and application started.
- `/api/health` returns HTTP 200 and database status `UP`.
- Dashboard loads from a separate device/network.
- Add menu item and customer; place a multi-item order; verify computed total.
- Change status and record a payment; confirm values remain after page refresh.
- Confirm the browser uses HTTPS and no credentials appear in GitHub or logs.

## E. Rollback / troubleshooting
- `Communications link failure`: check host, port, database allowlist/network and TLS options.
- `Access denied`: verify username/password and database grants.
- `Unknown database`: create the database and run `schema.sql`.
- App fails health check: inspect Railway logs; confirm schema and `DB_URL`.
- Do not mark deployment successful until the acceptance checklist passes.
