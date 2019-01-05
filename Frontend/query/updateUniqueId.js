var mysql = require("mysql");
var bcrypt = require("bcryptjs");
var pool = require("../db/index.js");

function setUniqueId(user, callback) {
  console.log("User details:" + JSON.stringify(user));
  var queryString = getQuery(user);

  console.log("Query:" + queryString);

  pool.getConnection((err, con) => {
    if (err) {
      console.log("Error occurred while creating a connection ", err);
      return callback(err, "Error occured");
    } else {
      con.query(queryString, (err, rows) => {
        if (err) {
          console.log("Error occurred while executing query ", err);
          return callback(err, "Error occured");
        } else {
          return callback(null, rows);
        }
      });
    }
  });
}

var getQuery = user => {
  let queryString =
    "Update zkp_user SET unique_doc_id =" +
    mysql.escape(user.unique_doc_id) +
    " ,  doc_type =" +
    mysql.escape(user.doc_type) +
    " ,  doc_name =" +
    mysql.escape(user.doc_name) +
    " WHERE id=" +
    mysql.escape(user.id);
  return queryString;
};

exports.setUniqueId = setUniqueId;
