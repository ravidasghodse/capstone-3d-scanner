function findpoint(num)
pts = load(sprintf('G:/capstone/lhspoint_%d.txt', num));

plot(pts(:,1), -pts(:, 2), '*');
size(pts)
axis([0 1920 -1080/2 0]);
hold on
pts = load(sprintf('G:/capstone/rhspoint_%d.txt', num));

plot(pts(:,1), -pts(:, 2), 'r*');
size(pts)