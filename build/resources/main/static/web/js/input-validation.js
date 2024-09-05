  function alphaOnly(e, t) {
      if (window.event) {
          var charCode = window.event.keyCode;
      }
      else if (e) {
          var charCode = e.which;
      }
     else { return true; }
     if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123))
         return true;
     else{ return false; }
  }

  function allowAlphaNumeric(event) {
       var code = ('charCode' in event) ? event.charCode : event.keyCode;
       if ( !(code > 47 && code < 58) && // numeric (0-9)
          !(code > 64 && code < 91) && // upper alpha (A-Z)
          !(code > 96 && code < 123)) { // lower alpha (a-z)
          event.preventDefault();
       }
  }

  function allowAlphaNumericWithSpace(event, id) {
        var code = ('charCode' in event) ? event.charCode : event.keyCode;
        if (event.which === 32 && !$("#"+id).val().length) {
              event.preventDefault();
        }
        if ( !(code > 47 && code < 58) && // numeric (0-9)
          !(code > 64 && code < 91) && // upper alpha (A-Z)
          !(code > 96 && code < 123) && // lower alpha (a-z)
          !(code == 32)) { // space
          event.preventDefault();
        }
  }
   function alphaWithSpaceOnly(event) {
          var code = ('charCode' in event) ? event.charCode : event.keyCode;
          if ( !(code > 64 && code < 91) && // upper alpha (A-Z)
                  !(code > 96 && code < 123) && // lower alpha (a-z)
                  !(code == 32)) { // space
                  event.preventDefault();
          }
   }
   function allowAlphaNumericWithSpaceUnderscore (event, id) {
        var code = ('charCode' in event) ? event.charCode : event.keyCode;
        if ( !(code > 47 && code < 58) && // numeric (0-9)
          !(code > 64 && code < 91) && // upper alpha (A-Z)
          !(code > 96 && code < 123) && // lower alpha (a-z)
          !(code == 32) && !(code == 95)) { // space underScore
          event.preventDefault();
        }
   }
   function minMaxInputLength(id, minLength, maxLength) {
        var value = $("#"+id).val();
        if (value.length >= minLength || value.length <= maxLength) {
            return true;
        }
        return false;
   }
   function validateIp(ip) {
            rgx = /\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/;
            return rgx.test((ip).toString());
   }

    function allowNumeric(e) {
             var code = ('charCode' in e) ? e.charCode : e.keyCode;
             if ( !(code > 47 && code < 58)) // numeric (0-9)
              {
               e.preventDefault();
             }
    }
    /*function validatePort(port) {
            if(Number(port)>0 && Number(port)<65536) {
                return true;
            }
             return false;
    }*/

    function ipValidation(fieldId){
        var pattern = /\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/;
        x = 46;
        $('#'+fieldId).keypress(function (e) {
            if (e.which != 8 && e.which != 0 && e.which != x && (e.which < 48 || e.which > 57)) {

                return false;
            }
        }).keyup(function () {
            var this1 = $(this);
            if (!pattern.test(this1.val())) {
                while (this1.val().indexOf("..") !== -1) {
                    this1.val(this1.val().replace('..', '.'));
                }
                x = 46;
            } else {
                x = 0;
                var lastChar = this1.val().substr(this1.val().length - 1);
                if (lastChar == '.') {
                    this1.val(this1.val().slice(0, -1));
                }
                var ip = this1.val().split('.');
                if (ip.length == 4) {
                }
            }
        });
    }
    function allowAlphaNumericWithHyphen (event, id) {
            var code = ('charCode' in event) ? event.charCode : event.keyCode;
            if (event.which == 32) {
                  event.preventDefault();
            }
            if ( !(code > 47 && code < 58) && // numeric (0-9)
              !(code > 64 && code < 91) && // upper alpha (A-Z)
              !(code > 96 && code < 123) && // lower alpha (a-z)
              !(code == 45)) { // hyphen
              event.preventDefault();
            }
       }