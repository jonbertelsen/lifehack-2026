
import express from 'express';
import cors from 'cors';

const app = express();
app.use(cors());
app.use(express.json());

const WORDS = ['APPLE', 'BANJO', 'CRANE', 'DOZER', 'EAGLE']; // midlertidig ordliste
const DAILY_WORD = WORDS[new Date().getDate() % WORDS.length]; // dagligt ord

function checkGuess(guess, word) {
    const result = [];
    const used = Array(word.length).fill(false);

    // Først: grøn
    for (let i = 0; i < guess.length; i++) {
        if (guess[i] === word[i]) {
            result[i] = 'green';
            used[i] = true;
        }
    }

    // Så: gul / grå
    for (let i = 0; i < guess.length; i++) {
        if (result[i]) continue;
        const idx = word.split('').findIndex((c, j) => c === guess[i] && !used[j]);
        if (idx >= 0) {
            result[i] = 'yellow';
            used[idx] = true;
        } else {
            result[i] = 'gray';
        }
    }

    return result;
}

app.post('/guess', (req, res) => {
    const { guess } = req.body;
    if (!guess || guess.length !== 5) {
        return res.status(400).json({ error: 'Guess must be 5 letters' });
    }

    const upperGuess = guess.toUpperCase();
    const result = checkGuess(upperGuess, DAILY_WORD);
    res.json({ result });
});

app.listen(7000, () => console.log('Server running on port 3000'));