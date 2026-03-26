const Q = document.getElementById("q");
const W = document.getElementById("w");
const E = document.getElementById("e");

Q.addEventListener("click", () => {
    try{
        document.getElementById("1Input").innerHTML = "Q";
    } catch (e){
        console.error("Du er lort");
    }
});

W.addEventListener("click", () => {
    try{
        document.getElementById("2Input").innerHTML = "W";
    } catch (e){
        console.error("Du er lort");
    }
});

E.addEventListener("click", () => {
    try{
        document.getElementById("3Input").innerHTML = "E";
    } catch (e){
        console.error("Du er lort");
    }
});