const Q = document.getElementById("q");
const W = document.getElementById("w");
const E = document.getElementById("e");

Q.addEventListener("click", () => {
    try{
        document.getElementById("input1").innerHTML = "Q";
    } catch (e){
        console.error("Du er lort");
    }
});

W.addEventListener("click", () => {
    try{
        document.getElementById("input2").innerHTML = "W";
    } catch (e){
        console.error("Du er lort");
    }
});

E.addEventListener("click", () => {
    try{
        document.getElementById("input3").innerHTML = "E";
    } catch (e){
        console.error("Du er lort");
    }
});