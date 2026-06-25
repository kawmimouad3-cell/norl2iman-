fetch("https://habous.gov.ma/prieres/prieres.php?ville=34")
  .then(response => response.text())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
