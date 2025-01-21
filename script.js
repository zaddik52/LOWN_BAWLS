document.getElementById("namesForm").addEventListener("submit", function(event) {
    event.preventDefault();
    
    // קבלת השמות מתוך השדות
    const names = [];
    const nameInputs = document.querySelectorAll('input[name="name"]');
    nameInputs.forEach(input => {
        names.push(input.value);
    });

    // בדיקה אם יש בדיוק 24 שמות
    if (names.length !== 24) {
        alert("אנא הזן בדיוק 24 שמות");
        return;
    }

    // ערבוב השמות בצורה אקראית
    for (let i = names.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [names[i], names[j]] = [names[j], names[i]]; // החלפה
    }

    // חלוקה ל-6 קבוצות של 4 אנשים
    const groups = [];
    for (let i = 0; i < 6; i++) {
        groups.push(names.slice(i * 4, i * 4 + 4));
    }

    // הצגת התוצאה
    const groupsContainer = document.getElementById("groups");
    groupsContainer.innerHTML = '';  // ניקוי תוכן קודם
    groups.forEach((group, index) => {
        const groupDiv = document.createElement("div");
        groupDiv.innerHTML = `<strong>קבוצה ${index + 1}:</strong> ${group.join(', ')}`;
        groupsContainer.appendChild(groupDiv);
    });
});
