let loginBtn = document.getElementById("loginBtn");
let loginDiv = document.getElementById("login_div");
let logoutBtn = document.getElementById("logoutBtn");
let requestFormBtn = document.getElementById("getRequestForm");
let newRequestForm = document.getElementById("newRequestForm");
let submitRequestBtn = document.getElementById("submitRequestBtn");

let wholeTableReimb = document.getElementById("tableReimbursements");
let reimbHead = document.getElementById("reimbHead");
let tableAllReimb = document.getElementById("tableReimb");
let showRequestDiv = document.getElementById("showRequestDiv");
let buttonShowAll = document.getElementById("reimbBtn");
let requestStatus = document.getElementById("reqStatus");

let aprvDiv = document.getElementById("approveDenyDiv");
let aprvForm = document.getElementById("approvalForm");
let aprvBtn = document.getElementById("aprvBtn");
let aprvText = document.getElementById("requestID");
let aprvSelect = document.getElementById("selectAction");

const url = "http://localhost:3004/";

loginBtn.addEventListener("click", loginFunc);
logoutBtn.addEventListener("click", logoutFunc);
requestFormBtn.addEventListener("click", createRequestFunc);
submitRequestBtn.addEventListener("click", submitRequest);
buttonShowAll.addEventListener("click", getRequests);
aprvBtn.addEventListener("click", resolveRequest);

async function loginFunc(){
    let user = {
        username: document.getElementById("username_input").value,
        password: document.getElementById("password_input").value
      }

    let response = await fetch(
      url+"login",
      {
        method : "POST",
        body : JSON.stringify(user),
        credentials: "include"
      }
    );

    if (response.status === 200){
        console.log("The login post request succeeded");
        let userRole = getCookie("userRole");
        if (userRole === "Finacial Manager"){
          console.log("The current user is a Manager");
          aprvDiv.style.display = "block";
        }else{
          console.log("The current user is an Associate");
        }
        wholeTableReimb.style.display = "table";
        requestFormBtn.style.display = "inline";
        loginDiv.style.display = "none";
        logoutBtn.style.display = "inline";
        showRequestDiv.style.display = "block";
        requestStatus.style.display = "inline";
    }else{
        console.log("Login unsuccessful");
    }
}

async function logoutFunc(){
  let response = await fetch(url + "logout");

  if (response.status === 200){
      loginDiv.style.display = "block";
      wholeTableReimb.style.display = "none";
      logoutBtn.style.display = "none";
      aprvDiv.style.display = "none";
      showRequestDiv.style.display = "none";
      requestFormBtn.style.display = "none";
      newRequestForm.style.display = "none";
      console.log("Logout successful");
  }else{
      console.log("Logout unsuccessful");
  }
}

function getCookie(cname) {
  let name = cname + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  let ca = decodedCookie.split(';');
  for(let i = 0; i <ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

async function resolveRequest(){
  let resolveChoice = aprvSelect.value;
  let requestID = parseInt(aprvText.value);

  if (requestID > 0 && (resolveChoice.toLowerCase() === "approve" || resolveChoice.toLowerCase() === "deny")){
    let request = {
      resolveChoice: resolveChoice,
      requestID: requestID
    }

    let response = await fetch(url+"requests/resolve", {
        method:"PATCH",
        body:JSON.stringify(request),
        credentials:"include"
      })
    
    if(response.status===200){
      console.log("Request approved/denied successfully");
    }else{
      console.log("Problem encountered when approving/denying the request.");
    }
  }else{
    console.log("At least one of the values provided are not valid");
  }

}

async function submitRequest(){
  let requestAmount = document.getElementById("requestAmount").value;
  let requestType = document.getElementById("requestType").value;
  let requestDescription = document.getElementById("requestDescription").value;

  if (requestAmount > 0 && requestDescription != ""){
    let request = {
      amount: requestAmount,
      type: requestType,
      description: requestDescription,
    }

    let response = await fetch(url+"requests/add", {
      method:"POST",
      body:JSON.stringify(request),
      credentials:"include"
    })
  
  if(response.status===201){
    console.log("Request added successfully");
    requestFormBtn.style.display = "inline";
    newRequestForm.style.display = "none";
  }else{
    console.log("Problem encountered when adding the request.");
  }
  }else{
    console.console.log("One of the fields are empty");
  }
}

function createRequestFunc(){
    newRequestForm.style.display = "block";
    requestFormBtn.style.display = "none";
}

async function getRequests(){
  let endPoint = url;
  
  if(requestStatus.value == "pending"){
    endPoint = endPoint+ "requests/Pending";
  }
  else if(requestStatus.value == "all"){
    endPoint = endPoint+ "requests/all";
  }
  else if(requestStatus.value == "approved"){
    endPoint = endPoint+ "requests/Approved";
  }
  else if(requestStatus.value == "denied"){
    endPoint = endPoint+ "requests/Denied";
  }
  else {
    console.log("Incorrect request status");
  }

    let response = await fetch(endPoint, {
      credentials:"include"
    });
  

  if(response.status===200){
    let records = await response.json();
    console.log("The list of requests was retrieved");
    populateRequests(records);
  } else if (response.status===204){
    console.log("The list of requests is empty");
  }else{
    console.log("There was an error getting your requests.");
  }
     
}

function populateRequests(requests){
    tableAllReimb.innerHTML = "";
    for (let request of requests){
        let row = document.createElement("tr");
        for (let data in request){
            let td = document.createElement("td");
            td.innerText = request[data];
            row.appendChild(td);
        }
        tableAllReimb.appendChild(row);
    }
}