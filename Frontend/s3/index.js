const multerS3 = require("multer-s3");
const aws = require("aws-sdk");
const multer = require("multer");
const uniqid = require("uniqid");
var CONST = require("../const");
var update = require("../query/updateUniqueId");

aws.config.update({
  secretAccessKey: CONST.awsS3Key,
  accessKeyId: CONST.awsS3Id,
  region: CONST.awsS3Region
});

var s3 = new aws.S3();
var uniqueId = "";
const upload = multer({
  storage: multerS3({
    s3: s3,
    bucket: CONST.ResumesBucket,
    key: function(req, file, cb) {
      console.log("req ", JSON.stringify(req.body));
      console.log(file);
      uniqueId = uniqid();
      var filenameForDB = uniqueId + "_" + req.body.docType + "_" + Date.now();
      var fileNameForS3 = uniqueId + "_" + req.body.docType + "_" + Date.now() + ".pdf";

      console.log("filenameForDB:" + filenameForDB);
      console.log("fileNameForS3:" + fileNameForS3);
      
      var user = {};

      user.id = req.session.user.id;
      user.unique_doc_id = uniqueId;
      user.doc_type=req.body.docType;
      user.doc_name=filenameForDB;

      console.log("User" + user);
      update.setUniqueId(user, (err, result) => {
        if (err) {
          console.log("Error occured");
        } else {
          console.log("updated uniqueid successfully");
        }
      });
      cb(null, fileNameForS3);
    }
  })
});

module.exports = { upload };
