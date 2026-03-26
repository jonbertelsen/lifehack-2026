function spin() {
    const inputs = document.querySelectorAll(".option-input");
    let values = [];

    inputs.forEach(input => {
        if (input.value.trim() !== "") {
            values.push(input.value);
        }
    });

    if (values.length === 0) {
        alert("Skriv mindst én mulighed!");
        return;
    }

    const randomIndex = Math.floor(Math.random() * values.length);
    const result = values[randomIndex];

    document.getElementById("resultText").innerText = result;
}

function reset() {
    document.getElementById("resultText").innerText = "RESULTAT";

    const inputs = document.querySelectorAll(".option-input");
    inputs.forEach(input => input.value = "");
}