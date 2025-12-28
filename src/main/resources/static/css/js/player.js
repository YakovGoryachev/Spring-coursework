// Глобальные переменные
let currentTrackId = null;
let trackList = [];
let currentTrackIndex = -1;
let lastPlayedTrackId = null;

const audio = document.getElementById('global-audio');
const playIcon = document.getElementById('play-icon');
const currentTrackTitleEl = document.getElementById('current-track');
const progressBar = document.getElementById('progress');
const progressContainer = document.getElementById('progress-bar');
const currentTimeEl = document.getElementById('current-time');
const totalTimeEl = document.getElementById('total-time');

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    // Восстанавливаем состояние из localStorage
    const saved = JSON.parse(localStorage.getItem('musicStreamPlayer')) || {};
    if (saved.trackId) {
        loadTrackById(saved.trackId);
        if (saved.isPlaying) {
            setTimeout(() => audio.play().catch(e => console.log("Auto-play blocked")), 500);
        }
        if (saved.currentTime) {
            audio.currentTime = saved.currentTime;
        }
        if (saved.trackList) {
            trackList = saved.trackList;
            currentTrackIndex = saved.currentTrackIndex;
        }
    }

    // Слушатели событий
    setupEventListeners();
});

function setupEventListeners() {
    audio.addEventListener('play', () => {
        playIcon.classList.remove('fa-play');
        playIcon.classList.add('fa-pause');
        savePlayerState();
    });

    audio.addEventListener('pause', () => {
        playIcon.classList.remove('fa-pause');
        playIcon.classList.add('fa-play');
        savePlayerState();
    });

    audio.addEventListener('timeupdate', () => {
        const ct = audio.currentTime;
        const dur = audio.duration || 0;
        const min = Math.floor(ct / 60);
        const sec = Math.floor(ct % 60);
        const totalMin = Math.floor(dur / 60);
        const totalSec = Math.floor(dur % 60);

        currentTimeEl.textContent = `${min}:${sec < 10 ? '0' : ''}${sec}`;
        totalTimeEl.textContent = `${totalMin}:${totalSec < 10 ? '0' : ''}${totalSec}`;

        if (dur > 0) {
            const percent = (ct / dur) * 100;
            progressBar.style.width = percent + '%';
        }
    });

    audio.addEventListener('ended', () => {
        skipForward(); // ← теперь работает по очереди
    });

    // Клик по прогресс-бару
    progressContainer.addEventListener('click', (e) => {
        const rect = progressContainer.getBoundingClientRect();
        const clickX = e.clientX - rect.left;
        const width = rect.width;
        const percent = clickX / width;
        audio.currentTime = percent * audio.duration;
    });
}

function savePlayerState() {
    localStorage.setItem('musicStreamPlayer', JSON.stringify({
        trackId: currentTrackId,
        isPlaying: !audio.paused,
        currentTime: audio.currentTime,
        trackList: trackList,
        currentTrackIndex: currentTrackIndex
    }));
}

// Основная функция: загрузить трек по ID
function loadTrackById(trackId) {
    if (!trackId) return;

    fetch(`/api/tracks/${trackId}/info`)
        .then(res => res.json())
        .then(data => {
            audio.src = `/audio/${trackId}`;
            currentTrackId = trackId;
            currentTrackTitleEl.textContent = `${data.title} - ${data.artist}`;
            savePlayerState();
        })
        .catch(err => console.error('Failed to load track info', err));
}

// Внешний API: вызывается из кнопок на страницах
function playTrack(trackId, title, artist) {
    // Обновляем очередь
    if (!trackList.some(t => t.id === trackId)) {
        trackList.push({ id: trackId, title, artist });
    }

    // Находим индекс текущего трека в очереди
    currentTrackIndex = trackList.findIndex(t => t.id === trackId);

    currentTrackId = trackId;
    currentTrackTitleEl.textContent = `${title} - ${artist}`;
    audio.src = `/audio/${trackId}`;

    // Увеличиваем счётчик ТОЛЬКО если это новый трек
    if (lastPlayedTrackId !== trackId) {
        fetch(`/api/tracks/${trackId}/play`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                console.log('Play count incremented');
                lastPlayedTrackId = trackId;
            } else {
                console.error('Failed to increment play count');
            }
        })
        .catch(err => console.error('Error incrementing play count:', err));
    }

    audio.play().catch(e => console.log("Auto-play blocked"));
}

function togglePlay() {
    if (audio.paused) {
        audio.play().catch(e => console.log("Auto-play blocked"));
    } else {
        audio.pause();
    }
}

function changeVolume(value) {
    audio.volume = value / 100;
}

function skipForward() {
    if (trackList.length === 0) return;

    if (currentTrackIndex < trackList.length - 1) {
        currentTrackIndex++;
    } else {
        currentTrackIndex = 0; // зацикливание
    }

    const nextTrack = trackList[currentTrackIndex];
    playTrack(nextTrack.id, nextTrack.title, nextTrack.artist);
}

function skipBackward() {
    if (trackList.length === 0) return;

    if (currentTrackIndex > 0) {
        currentTrackIndex--;
    } else {
        currentTrackIndex = trackList.length - 1; // зацикливание
    }

    const prevTrack = trackList[currentTrackIndex];
    playTrack(prevTrack.id, prevTrack.title, prevTrack.artist);
}

document.addEventListener('click', (e) => {
    if (e.target.closest('.play-track-btn')) {
        const btn = e.target.closest('.play-track-btn');
        const id = btn.dataset.trackId;
        const title = btn.dataset.title;
        const artist = btn.dataset.artist;
        playTrack(id, title, artist);
    }
});

// Добавляем в начало player.js, после глобальных переменных
function updateTrackList(newTracks) {
    // Преобразуем треки в нужный формат
    trackList = newTracks.map(track => ({
        id: track.id,
        title: track.title || track.name,
        artist: track.artist || track.artistName
    }));

    // Сохраняем очередь в localStorage
    savePlayerState();

    console.log('Track list updated:', trackList);
}

window.updateTrackList = updateTrackList;