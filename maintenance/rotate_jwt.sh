NEW_SECRET=$(openssl rand -base64 48)

sed -i.bak "s|^jwt\.secret=.*|jwt.secret=${NEW_SECRET}|" ../api_v1/src/main/resources/application.properties

echo "JWT secret rotated"

